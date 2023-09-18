package com.xforceplus.wapp.modules.invoicetaxmapping.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;

import java.util.Map;

public interface InvoiceTaxMappingService extends IService<TInvoiceTaxMappingEntity> {
    Page<TInvoiceTaxMappingEntity> paged(InvoiceTaxMappingQuery vo);
    /**
     * 列表查询统计
     * @param map
     * @return
     */
    int queryListCount(Map<String, Object> map);

    /**
     * 添加
     * @param tInvoiceTaxMappingEntity
     * @return
     */
    int add(TInvoiceTaxMappingEntity tInvoiceTaxMappingEntity);

    /**
     * 修改
     * @param tInvoiceTaxMappingEntity
     */
    void edit(TInvoiceTaxMappingEntity tInvoiceTaxMappingEntity);

    void deleteMapping(int[] ids);
}
