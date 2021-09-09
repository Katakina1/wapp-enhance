package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/1 18:35
 */

public interface PrintingOpenRedTicketDataService {


    Integer getRedTicketMatchListCount(Map<String, Object> map);

    List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map);

    List<InvoiceDetail> getList(String params);

    ReportStatisticsEntity queryTotalResult(String params);

    void exportPdf(Map<String,Object> params, HttpServletResponse response);

    InvoiceEntity getPdfDate(String redTicketDataSerialNumber);

}
