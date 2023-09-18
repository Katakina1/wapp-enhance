package com.xforceplus.wapp.modules.invoicetaxmapping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;
import com.xforceplus.wapp.modules.invoicetaxmapping.service.InvoiceTaxMappingService;
import com.xforceplus.wapp.repository.dao.TInvoiceTaxMappingDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Slf4j
@Service
public class InvoiceTaxMappingServiceImpl extends ServiceImpl<TInvoiceTaxMappingDao, TInvoiceTaxMappingEntity> implements InvoiceTaxMappingService {

    @Autowired
    private TInvoiceTaxMappingDao tInvoiceTaxMappingDao;

    @Override
    public Page<TInvoiceTaxMappingEntity> paged(InvoiceTaxMappingQuery vo) {
        LambdaQueryWrapper<TInvoiceTaxMappingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(vo.getGoodsTaxNo()), TInvoiceTaxMappingEntity::getGoodsTaxNo, vo.getGoodsTaxNo());
        queryWrapper.eq(StringUtils.isNotEmpty(vo.getInvoiceType()), TInvoiceTaxMappingEntity::getInvoiceType, vo.getInvoiceType());
        queryWrapper.gt(!ObjectUtils.isEmpty(vo.getId()), TInvoiceTaxMappingEntity::getId, vo.getId());
        Page<TInvoiceTaxMappingEntity> pageRsult = this.page(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
        return pageRsult;
    }

    /**
     * 列表查询统计
     * @param params
     * @return
     */
    @Override
    public int queryListCount(Map<String, Object> params) {
        return tInvoiceTaxMappingDao.queryListCount(params);
    }

    /**
     * 新增
     * @param tInvoiceTaxMappingEntity
     * @return
     */
    @Override
    public int add(TInvoiceTaxMappingEntity tInvoiceTaxMappingEntity) {
        return tInvoiceTaxMappingDao.add(tInvoiceTaxMappingEntity);
    }

    @Override
    public void edit(TInvoiceTaxMappingEntity tInvoiceTaxMappingEntity) {
        tInvoiceTaxMappingDao.edit(tInvoiceTaxMappingEntity);
    }

    @Override
    public void deleteMapping(int[] ids) {
        tInvoiceTaxMappingDao.deleteMapping(ids);
    }
}
