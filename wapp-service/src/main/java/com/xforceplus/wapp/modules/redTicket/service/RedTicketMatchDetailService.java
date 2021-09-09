package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;

import java.util.List;
import java.util.Map;

public interface RedTicketMatchDetailService {
    /**
     * 红冲发票明细
     */
    Map<String, Object> updateInvoiceDetaillist(RedTicketMatchDetail params,Integer userId);
    /**
     * 查询红冲发票明细条数
     */
    Integer invoiceDetailsRedRushCount(Map<String, Object> map);

}
