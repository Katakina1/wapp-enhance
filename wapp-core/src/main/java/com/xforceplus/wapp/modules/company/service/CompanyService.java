package com.xforceplus.wapp.modules.company.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.CompanyTypeEnum;
import com.xforceplus.wapp.modules.blackwhitename.constants.Constants;
import com.xforceplus.wapp.modules.company.convert.CompanyConverter;
import com.xforceplus.wapp.modules.company.dto.CompanyImportDto;
import com.xforceplus.wapp.modules.company.dto.CompanyUpdateRequest;
import com.xforceplus.wapp.modules.company.listener.CompanyImportListener;
import com.xforceplus.wapp.modules.sys.dao.BaseUserDao;
import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TAcOrgDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 索赔的结算单相关逻辑操作
 */
@Service
@Slf4j
public class CompanyService extends ServiceImpl<TAcOrgDao, TAcOrgEntity> {
    private final CompanyConverter companyConverter;
    @Value("${wapp.export.tmp}")
    private String tmp;
    @Autowired
    private BaseUserDao baseUserDao;

    Cache<String, TAcOrgEntity> tAcOrgCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public CompanyService(CompanyConverter companyConverter) {
        this.companyConverter = companyConverter;
    }

    public Tuple2<List<TAcOrgEntity>, Page<TAcOrgEntity>> page(Long current, Long size, String taxNo) {
        LambdaQueryChainWrapper<TAcOrgEntity> wrapper = new LambdaQueryChainWrapper<TAcOrgEntity>(baseMapper);
        if (StringUtils.isNotBlank(taxNo)) {
            wrapper.eq(TAcOrgEntity::getTaxNo, taxNo);
        }

        Page<TAcOrgEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("抬头信息分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(companyConverter.map(page.getRecords()), page);
    }

    public TAcOrgEntity getByOrgCode(@NotNull(message = "获取机构时，机构代码不能为空") String orgCode, boolean isSeller) {
        if (isSeller) {
            return getOrgInfoByOrgCode(orgCode, "8");
        }
        return getOrgInfoByOrgCode(orgCode, "5");
    }

    public TAcOrgEntity getByTaxNo(String taxNo, String userCode) {
        QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<TAcOrgEntity>();
        if (StringUtils.isNotBlank(taxNo)) {
            wrapper.eq(TAcOrgEntity.TAX_NO, taxNo);
        }
//        if(Objects.nonNull(UserUtil.getUser())&&StringUtils.isNotEmpty(UserUtil.getUser().getUsercode())){
//            wrapper.eq(TAcOrgEntity.ORG_CODE, UserUtil.getUser().getUsercode());
//        }
        if (StringUtils.isNotEmpty(userCode)) {
            wrapper.eq(TAcOrgEntity.ORG_CODE, userCode);
        }
        wrapper.select("top 1 *");
        final List<TAcOrgEntity> list = list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public void update(CompanyUpdateRequest companyUpdateRequest) {
        LambdaUpdateChainWrapper<TAcOrgEntity> wrapper = new LambdaUpdateChainWrapper<TAcOrgEntity>(getBaseMapper());
        if (StringUtils.isNotBlank(companyUpdateRequest.getTaxNo())) {
            wrapper.eq(TAcOrgEntity::getTaxNo, companyUpdateRequest.getTaxNo());
        }
        if (StringUtils.isNotBlank(companyUpdateRequest.getAccount())) {
            wrapper.set(TAcOrgEntity::getAccount, companyUpdateRequest.getAccount());
        }
        if (StringUtils.isNotBlank(companyUpdateRequest.getBank())) {
            wrapper.set(TAcOrgEntity::getBank, companyUpdateRequest.getBank());
        }
        if (companyUpdateRequest.getQuota() != null) {
            wrapper.set(TAcOrgEntity::getQuota, companyUpdateRequest.getQuota());
        }
        if (StringUtils.isNotBlank(companyUpdateRequest.getTaxName())) {
            wrapper.set(TAcOrgEntity::getOrgName, companyUpdateRequest.getTaxName());
        }
        if (StringUtils.isNotBlank(companyUpdateRequest.getTaxName())) {
            wrapper.set(TAcOrgEntity::getTaxName, companyUpdateRequest.getTaxName());
        }
        if (StringUtils.isNotBlank(companyUpdateRequest.getPhone())) {
            wrapper.set(TAcOrgEntity::getPhone, companyUpdateRequest.getPhone());
        }
        if (StringUtils.isNotBlank(companyUpdateRequest.getAddress())) {
            wrapper.set(TAcOrgEntity::getAddress, companyUpdateRequest.getAddress());
        }
        if (Objects.nonNull(companyUpdateRequest.getTaxDeviceType())) {
            wrapper.set(TAcOrgEntity::getTaxDeviceType, companyUpdateRequest.getTaxDeviceType());
        }
        wrapper.set(TAcOrgEntity::getLastModifyTime, DateUtils.obtainValidDate(new Date()));
        wrapper.set(TAcOrgEntity::getLastModifyBy, UserUtil.getLoginName());
        wrapper.update();
//        if (flag) {
//            tAcOrgCache.invalidateAll();
//        }
        tAcOrgCache.invalidateAll();
    }

    /**
     * 根据税号和UserCode查询税号信息
     *
     * @param taxNo
     * @param userCode
     * @return
     */
    public TAcOrgEntity newGetByTaxNoAndUserCode(String taxNo, String userCode) {
        QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<>();
        UserEntity userEntity = getOrgIdByUserCode(userCode);
        if (userEntity == null) {
            return null;
        }
        wrapper.eq(TAcOrgEntity.ORG_ID, getOrgIdByUserCode(userCode).getOrgid());
        TAcOrgEntity acOrgEntity = this.getOne(wrapper);
        if (acOrgEntity != null && taxNo != null && StringUtils.equalsIgnoreCase(acOrgEntity.getTaxNo().trim(), taxNo.trim())) {
            return acOrgEntity;
        }
        return null;
    }

    /**
     * 根据orgcode获取公司信息
     *
     * @param orgCode --jvcode 或者供应商6d号码
     * @param orgType 5 沃尔玛公司 8供应商公司
     * @return
     */
    public TAcOrgEntity getOrgInfoByOrgCode(String orgCode, String orgType) {
        if (StringUtils.isEmpty(orgCode)) {
            return null;
        }
        try {
            String key = String.format("getOrgInfoByOrgCode:%s:%s", orgCode, Optional.ofNullable(orgType).orElse(""));
            return tAcOrgCache.get(key, () -> {
                //2022-09-15修改，先查询用户表中的信息，再查org表中的信息
                UserEntity userEntity = getOrgIdByUserCode(orgCode);
                TAcOrgEntity acOrgEntity = null;
                if (userEntity != null) {
                    QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<>();
                    wrapper.eq(TAcOrgEntity.ORG_ID, userEntity.getOrgid());
                    if (StringUtils.isNotBlank(orgType)) {
                        wrapper.eq(TAcOrgEntity.ORG_TYPE, orgType);
                    }
                    acOrgEntity = this.getOne(wrapper);
                    if (acOrgEntity != null) {
                        acOrgEntity.setTaxName(userEntity.getUsername());
                        acOrgEntity.setOrgName(userEntity.getUsername());
                    }
                    log.info("getOrgInfoByOrgCode orgCode:{}, orgType:{}, acOrgEntity:{}, userEntity:{}", orgCode, orgType,
                            JSON.toJSONString(acOrgEntity), JSON.toJSONString(userEntity));
                } else {
                    QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<>();
                    wrapper.eq(TAcOrgEntity.ORG_CODE, orgCode);
                    if (StringUtils.isNotBlank(orgType)) {
                        wrapper.eq(TAcOrgEntity.ORG_TYPE, orgType);
                    }
                    List<TAcOrgEntity> acOrgEntityList = this.list(wrapper);
                    acOrgEntity = acOrgEntityList != null && acOrgEntityList.size() > 0 ? acOrgEntityList.get(0) : null;
                    log.info("getOrgInfoByOrgCode orgCode:{}, orgType:{}, acOrgEntity:{}", orgCode, orgType, JSON.toJSONString(acOrgEntity));
                }
                return acOrgEntity;
            });
        } catch (Exception e) {
            log.error("orgCode:{},orgType:{},getOrgInfoByOrgCode:", orgCode, orgType, e);
        }
        return null;
    }

    private UserEntity getOrgIdByUserCode(String userCode) {
        List<UserEntity> entities = baseUserDao.queryByUsercode(null, userCode);
        if (entities != null && entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public TAcOrgEntity getPurchaserOrgInfoByTaxNo(String taxNo) {
        return getOrgInfoByTaxNo(taxNo, Constants.COMPANY_TYPE_WALMART);
    }

    public TAcOrgEntity getSellerOrgInfoByTaxNo(String taxNo) {
        return getOrgInfoByTaxNo(taxNo, Constants.COMPANY_TYPE_SUPPLIER);
    }

    /**
     * 根据orgcode获取公司信息
     *
     * @param taxNo   --jvcode 或者供应商6d号码
     * @param orgType 5 沃尔玛公司 8供应商公司
     * @return
     */
    public TAcOrgEntity getOrgInfoByTaxNo(String taxNo, String orgType) {
        if (StringUtils.isEmpty(taxNo)) {
            return null;
        }
        try {
            return tAcOrgCache.get(String.format("getOrgInfoByTaxNo:%s:%s", taxNo, Optional.ofNullable(orgType).orElse("")), () -> {
                QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<>();
                wrapper.eq(TAcOrgEntity.TAX_NO, taxNo);
                if (StringUtils.isNotBlank(orgType)) {
                    wrapper.eq(TAcOrgEntity.ORG_TYPE, orgType);
                }
                return Optional.ofNullable(this.page(new Page<>(1, 1), wrapper)).filter(x -> CollectionUtils.isNotEmpty(x.getRecords())).map(x -> x.getRecords().get(0))
                        .orElse(null);
            });
        } catch (Exception e) {
            log.error("获取组织信息失败:" + e.getMessage());
        }
        return null;
    }

    public boolean updateCompanyInfoById(TAcOrgEntity orgEntity) {
        if (null == orgEntity.getOrgId()) {
            return false;
        }
        LambdaUpdateChainWrapper<TAcOrgEntity> wrapper = new LambdaUpdateChainWrapper<>(getBaseMapper());

        wrapper.set(TAcOrgEntity::getLastModifyTime, DateUtils.obtainValidDate(new Date()));
        if (null != orgEntity.getOrgId()) {
            wrapper.eq(TAcOrgEntity::getOrgId, orgEntity.getOrgId());
        }
        if (StringUtils.isNotBlank(orgEntity.getAccount())) {
            wrapper.set(TAcOrgEntity::getAccount, orgEntity.getAccount());
        }
        if (StringUtils.isNotBlank(orgEntity.getAddress())) {
            wrapper.set(TAcOrgEntity::getAddress, orgEntity.getAddress());
        }
        if (StringUtils.isNotBlank(orgEntity.getBank())) {
            wrapper.set(TAcOrgEntity::getBank, orgEntity.getBank());
        }
        if (null != orgEntity.getQuota()) {
            wrapper.set(TAcOrgEntity::getQuota, orgEntity.getQuota());
        }
        if (StringUtils.isNotBlank(orgEntity.getOrgName())) {
            wrapper.set(TAcOrgEntity::getOrgName, orgEntity.getOrgName());
        }
        return wrapper.update();
    }

    /**
     * .导入抬头信息
     *
     * @param file
     * @return
     */
    public Either<String, Integer> importData(MultipartFile file) throws IOException {
        QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<>();
        CompanyImportListener listener = new CompanyImportListener();
        EasyExcel.read(file.getInputStream(), CompanyImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices())) {
            return Either.left("未解析到数据");
        }
        log.info("导入数据解析条数:{}", listener.getRows());
        wrapper.eq(TAcOrgEntity.ORG_TYPE, CompanyTypeEnum.COMPANY_TYPE_PUR.getResultCode());
        TAcOrgEntity purEntity = getOne(wrapper);
        QueryWrapper<TAcOrgEntity> wrapperSur = new QueryWrapper<>();
        wrapperSur.eq(TAcOrgEntity.ORG_TYPE, CompanyTypeEnum.COMPANY_TYPE_SUR.getResultCode());
        TAcOrgEntity surEntity = getOne(wrapper);
        List<String> supplierCodeList = listener.getValidInvoices().stream().map(CompanyImportDto::getSupplierCode).collect(Collectors.toList());
        List<CompanyImportDto> list = listener.getValidInvoices();
        QueryWrapper<TAcOrgEntity> wrapperCode = new QueryWrapper<>();
        wrapperCode.in(TAcOrgEntity.ORG_CODE, supplierCodeList);
        List<TAcOrgEntity> resultOrgCodeList = this.list(wrapperCode);
        Map<String, Long> map = new HashMap<>();
        resultOrgCodeList.stream().forEach(code -> {
            map.put(code.getOrgCode(), code.getOrgId());
        });
        log.debug("导入数据新增数据:{}", list);
        log.info("导入数据新增条数:{}", list.size());
        List<TAcOrgEntity> resulitList = companyConverter.reverse(list, 1L);
        List<TAcOrgEntity> addList = new ArrayList<>();
        List<TAcOrgEntity> updateList = new ArrayList<>();
        resulitList.stream().forEach(e -> {
            if (Constants.COMPANY_TYPE_WALMART.equals(e.getOrgType())) {
                e.setParentId(purEntity.getParentId());
            }
            if (Constants.COMPANY_TYPE_SUPPLIER.equals(e.getOrgType())) {
                e.setParentId(surEntity.getParentId());
            }
            if (map.get(e.getOrgCode()) != null) {
                e.setOrgId(map.get(e.getOrgCode()));
                updateList.add(e);
            } else {
                e.setCreateTime(DateUtils.obtainValidDate(new Date()));
                addList.add(e);
            }
        });
        boolean save = saveBatch(addList, 2000);
        if (CollectionUtils.isNotEmpty(updateList)) {
            updateList.stream().forEach(update -> {
                updateCompanyInfoById(update);
            });
            tAcOrgCache.invalidateAll();
        }
        if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
            EasyExcel.write(tmp + file.getOriginalFilename(), CompanyImportDto.class).sheet("sheet1").doWrite(listener.getInvalidInvoices());

        }
        return save || CollectionUtils.isNotEmpty(updateList) ? Either.right(list.size()) : Either.right(0);
    }


    public List<TAcOrgEntity> getPurchaserOrgs() {
        LambdaQueryWrapper<TAcOrgEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(TAcOrgEntity::getOrgName, TAcOrgEntity::getTaxNo, TAcOrgEntity::getOrgCode);
        wrapper.eq(TAcOrgEntity::getOrgType, 5);

        return this.list(wrapper);

    }

}