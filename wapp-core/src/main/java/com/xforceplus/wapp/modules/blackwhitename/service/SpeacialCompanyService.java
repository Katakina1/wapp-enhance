package com.xforceplus.wapp.modules.blackwhitename.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.modules.blackwhitename.constants.Constants;
import com.xforceplus.wapp.modules.blackwhitename.convert.SpeacialCompanyConverter;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportDto;
import com.xforceplus.wapp.modules.blackwhitename.listener.SpeclialCompanyImportListener;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfBlackWhiteCompanyDao;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 黑白名单相关逻辑操作
 */
@Service
@Slf4j
public class SpeacialCompanyService extends ServiceImpl<TXfBlackWhiteCompanyDao, TXfBlackWhiteCompanyEntity> {
    private final SpeacialCompanyConverter companyConverter;
    @Value("${wapp.export.tmp}")
    private String tmp;

    public SpeacialCompanyService(SpeacialCompanyConverter companyConverter) {
        this.companyConverter = companyConverter;
    }

    public Page<TXfBlackWhiteCompanyEntity> page(Long current, Long size,String taxNo,String companyName,String type) {
        LambdaQueryChainWrapper<TXfBlackWhiteCompanyEntity> wrapper = new LambdaQueryChainWrapper<TXfBlackWhiteCompanyEntity>(baseMapper);
        wrapper.eq(TXfBlackWhiteCompanyEntity::getSupplierStatus,Constants.COMPANY_STATUS_ENABLED);
        if(StringUtils.isNotEmpty(taxNo)){
            wrapper.like(TXfBlackWhiteCompanyEntity::getSupplierTaxNo,taxNo);
        }
        if(StringUtils.isNotEmpty(companyName)){
            wrapper.like(TXfBlackWhiteCompanyEntity::getCompanyName,companyName);
        }
        if(StringUtils.isNotEmpty(type)){
            wrapper.like(TXfBlackWhiteCompanyEntity::getSupplierType,type);
        }
        Page<TXfBlackWhiteCompanyEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("黑白名单信息分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return page;
    }

    public TXfBlackWhiteCompanyEntity getBlackListBy6D(String supplier6d, String supplierType) {
        QueryWrapper<TXfBlackWhiteCompanyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfBlackWhiteCompanyEntity.SUPPLIER_6D, supplier6d);
        wrapper.eq(TXfBlackWhiteCompanyEntity.SUPPLIER_TYPE, supplierType);
        return getOne(wrapper);

    }

    /**
     * 导入黑白名单信息
     *
     * @param file
     * @return
     */
    public Either<String, Integer> importData(MultipartFile file, String type) throws IOException {
        QueryWrapper wrapper = new QueryWrapper<>();
        SpeclialCompanyImportListener listener = new SpeclialCompanyImportListener();
        EasyExcel.read(file.getInputStream(), SpecialCompanyImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices())) {
            return Either.left("未解析到数据");
        }
        log.info("导入数据解析条数:{}", listener.getRows());
        List<TXfBlackWhiteCompanyEntity> validList = companyConverter.reverse(listener.getValidInvoices(), UserUtil.getUserId());

        List<String> supplierCodeList = listener.getValidInvoices().stream().map(SpecialCompanyImportDto::getSupplierTaxNo).collect(Collectors.toList());
        QueryWrapper wrapperCode = new QueryWrapper<>();
        wrapperCode.in(TXfBlackWhiteCompanyEntity.SUPPLIER_TAX_NO, supplierCodeList);
        wrapperCode.eq(TXfBlackWhiteCompanyEntity.SUPPLIER_STATUS,Constants.COMPANY_STATUS_ENABLED);
        List<TXfBlackWhiteCompanyEntity> resultOrgCodeList = this.list(wrapperCode);
        Map<String, Long> map = new HashMap<>();
        resultOrgCodeList.stream().forEach(code -> {
            map.put(code.getSupplierTaxNo(), code.getId());
        });
        validList.stream().forEach(e -> {
            e.setSupplierType(type);
            e.setId(map.get(e.getSupplierTaxNo()));
            e.setSupplierStatus(Constants.COMPANY_STATUS_ENABLED);
        });
        boolean save = saveOrUpdateBatch(validList);
        if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
            EasyExcel.write(tmp + file.getOriginalFilename(), SpecialCompanyImportDto.class).sheet("sheet1").doWrite(listener.getInvalidInvoices());

        }
        return save ? Either.right(listener.getValidInvoices().size()) : Either.right(0);
    }


    /**
     * 判断供应商是否在黑白名单中
     *
     * @param supplierType {@link String} 0-黑名单 1-白名单
     * @param memo         供应商6D
     * @return
     */
    public boolean hitBlackOrWhiteList(String supplierType, String memo) {
        return 0 == count(
                new QueryWrapper<TXfBlackWhiteCompanyEntity>()
                        .lambda()
                        // 黑名单
                        .eq(TXfBlackWhiteCompanyEntity::getSupplierType, supplierType)
                        // 供应商6D
                        .eq(TXfBlackWhiteCompanyEntity::getSupplier6d, memo)
        );
    }

    /**
     * 批量删除
     * @param id
     */
    public void deleteById(Long[] id) {
        List<TXfBlackWhiteCompanyEntity> list = new ArrayList<>();
        for(int i=0;i<id.length;i++){
            TXfBlackWhiteCompanyEntity entity = new TXfBlackWhiteCompanyEntity();
            entity.setId(id[i]);
            entity.setSupplierStatus(Constants.COMPANY_STATUS_DELETE);
            list.add(entity);
        }
        this.updateBatchById(list);

    }
}
