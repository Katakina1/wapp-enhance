package com.xforceplus.wapp.modules.fixed.dao;

import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InvoiceImportExportDao {

    /**
     * 查询发票信息
     * @param map
     * @return
     */
    List<InvoiceImportAndExportEntity> invoiceImportAndExportlist(@Param("map") Map<String, Object> map);

    /**
     * 查询发票条数
     * @param map
     * @return
     */
    Integer invoiceImportAndExportlistCount(@Param("map") Map<String, Object> map);

    /**
     *
     * 查询全部
     * */
    List<InvoiceImportAndExportEntity> invoiceImportAndExportlistAll(@Param("map") Map<String, Object> map);


    void invoiceImportAndExportUpdate(@Param("map") Map<String, Object> map);

    List<String> selectOrdersById(@Param("id") Long id);
}
