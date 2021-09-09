package com.xforceplus.wapp.modules.fixed.dao;

import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SapUnconfirmDao {

    /**
     * 查询sap待确认信息
     * @param map
     * @return
     */
    List<InvoiceImportAndExportEntity> saplist(@Param("map") Map<String, Object> map);

    /**
     * 查询sap待确认条数
     * @param map
     * @return
     */
    Integer sapCount(@Param("map") Map<String, Object> map);

    /**
     * sap匹配状态修改为成功
     * @return
     */
    Integer sapSuccess(Long id);

    /**
     * 退票
     * @param param
     * @return
     */
    Integer refund(Map<String,Object> param);

    /**
     * 退票后修改扫描表isdel为1
     * @param param
     * @return
     */
    Integer refundInvoice(Map<String,Object> param);

}
