package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/10/27 10:05
 */

public interface EntryRedTicketService {


    List<RedTicketMatch> selectRedTicketList(Map<String,Object> map);
    Integer selectRedTicketListCount(Map<String,Object> map);

    List<OrgEntity> getGfNameAndTaxNo(Long userId);

    PagedQueryResult<InvoiceEntity> invoiceQueryOut(Map<String,Object> params);
    /**
     * 判断发票类型是否为普票
     * @param invoiceCode
     * @return
     */
    String getFplx(String invoiceCode);

    PagedQueryResult<InvoiceEntity> invoiceQueryList(Map<String,Object> params);

    RedTicketMatch selectRedTicketById(Map<String,Object> map);

    Map<String,Object> importInvoice(Map<String,Object> params, MultipartFile multipartFile);

    RedTicketMatch selectNoticeById(Map<String,Object> params);

    String getXfTaxno(Integer orgid);
}
