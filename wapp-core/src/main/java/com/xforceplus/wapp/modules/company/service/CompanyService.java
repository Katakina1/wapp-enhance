package com.xforceplus.wapp.modules.company.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.CompanyTypeEnum;
import com.xforceplus.wapp.modules.blackwhitename.constants.Constants;
import com.xforceplus.wapp.modules.company.convert.CompanyConverter;
import com.xforceplus.wapp.modules.company.dto.CompanyImportDto;
import com.xforceplus.wapp.modules.company.dto.CompanyUpdateRequest;
import com.xforceplus.wapp.modules.company.listener.CompanyImportListener;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TAcOrgDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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

    public TAcOrgEntity getByTaxNo(String taxNo) {
        QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<TAcOrgEntity>();
        if (StringUtils.isNotBlank(taxNo)) {
            wrapper.eq(TAcOrgEntity.TAX_NO, taxNo);
        }
        return getOne(wrapper);
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
        wrapper.set(TAcOrgEntity::getLastModifyTime, DateUtils.obtainValidDate(new Date()));
        wrapper.set(TAcOrgEntity::getLastModifyBy, UserUtil.getLoginName());
        wrapper.update();
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
        QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(orgCode)) {
            wrapper.eq(TAcOrgEntity.ORG_CODE, orgCode);
        }
        if (StringUtils.isNotBlank(orgType)) {
            wrapper.eq(TAcOrgEntity.ORG_TYPE, orgType);
        }
        return this.getOne(wrapper);
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
        QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(taxNo)) {
            wrapper.eq(TAcOrgEntity.TAX_NO, taxNo);
        }
        if (StringUtils.isNotBlank(orgType)) {
            wrapper.eq(TAcOrgEntity.ORG_TYPE, orgType);
        }
        return this.getOne(wrapper);
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
     * 导入抬头信息
     *
     * @param file
     * @return
     */
    public Either<String, Integer> importData(MultipartFile file) throws IOException {
        QueryWrapper wrapper = new QueryWrapper<>();
        CompanyImportListener listener = new CompanyImportListener();
        EasyExcel.read(file.getInputStream(), CompanyImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices())) {
            return Either.left("未解析到数据");
        }
        log.info("导入数据解析条数:{}", listener.getRows());
        wrapper.eq(TAcOrgEntity.ORG_TYPE, CompanyTypeEnum.COMPANY_TYPE_PUR.getResultCode());
        TAcOrgEntity purEntity = getOne(wrapper);
        QueryWrapper wrapperSur = new QueryWrapper<>();
        wrapperSur.eq(TAcOrgEntity.ORG_TYPE, CompanyTypeEnum.COMPANY_TYPE_SUR.getResultCode());
        TAcOrgEntity surEntity = getOne(wrapper);
        List<String> supplierCodeList = listener.getValidInvoices().stream().map(CompanyImportDto::getSupplierCode).collect(Collectors.toList());
        List<CompanyImportDto> list = listener.getValidInvoices();
        QueryWrapper wrapperCode = new QueryWrapper<>();
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
