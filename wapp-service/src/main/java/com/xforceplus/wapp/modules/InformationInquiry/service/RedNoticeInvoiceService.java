package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.modules.InformationInquiry.entity.RedNoticeBathEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/21 9:25
 */

public interface RedNoticeInvoiceService {
    Map<String,Object> parseExcel(MultipartFile multipartFile, String loginname,String redTicketType);

    List<RedNoticeBathEntity> queryList(Map<String,Object> map);

    int queryTotalResult(Map<String,Object> map);

    String createZip(List<RedNoticeBathEntity> list);

    void downloadPDF(String path, HttpServletResponse response);
}
