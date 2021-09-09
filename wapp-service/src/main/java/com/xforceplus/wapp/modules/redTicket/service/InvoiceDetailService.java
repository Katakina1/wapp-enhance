package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;

import java.util.List;
import java.util.Map;


public interface InvoiceDetailService {
    /**
     *查询未红冲发票明细信息
     * @param params
     * @return
     */
    List<InvoiceDetail> getInvoiceDetaillist(Map<String, Object> params);

    /**
     * 查询未红冲的发票明细信息条数
     */
    Integer invoiceDetailsCount(Map<String, Object> map);
}
