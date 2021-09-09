package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InvoiceDetailDao {
    /**
     *查询未红冲发票明细信息
     * @param params
     * @return
     */
    List<InvoiceDetail> getInvoiceDetaillist(@Param("map") Map<String, Object> params);

    /**
     * 查询未红冲的发票明细信息条数
     */
    Integer invoiceDetailsCount(@Param("map") Map<String, Object> map);

    /**
     * 红冲发票明细
     */
    Integer redRushInvoiceDetails(@Param("map") InvoiceDetail map,@Param("redTicketNumber") String redTicketNumber);
    /**
     * 取消发票明细红冲
     */
    String updateInvoiceDetails(@Param("map") Map<String, Object> map);
}
