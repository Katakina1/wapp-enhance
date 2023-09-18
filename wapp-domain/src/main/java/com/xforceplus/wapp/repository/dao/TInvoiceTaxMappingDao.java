package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.repository.entity.TaxCodeManageEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface TInvoiceTaxMappingDao extends BaseMapper<TInvoiceTaxMappingEntity> {

    int add(@Param("entity") TInvoiceTaxMappingEntity tInvoiceTaxMappingEntity);

    void edit(@Param("entity") TInvoiceTaxMappingEntity tInvoiceTaxMappingEntity);
    int deleteMapping(@Param("ids") int[] ids);
    int queryListCount(Map<String, Object> params);
}
