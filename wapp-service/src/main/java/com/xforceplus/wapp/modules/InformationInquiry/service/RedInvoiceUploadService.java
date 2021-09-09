package com.xforceplus.wapp.modules.InformationInquiry.service;



import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface RedInvoiceUploadService {

    /**
     * 分页列表
     * @param map
     * @return
     */
    List<RedInvoiceUploadEntity> queryList(Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(Map<String, Object> map);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<RedInvoiceUploadEntity> queryListAll(Map<String, Object> map);

    public Map<String, Object> parseExcel(MultipartFile multipartFile);

    List<RedInvoiceUploadExcelEntity> transformExcle(List<RedInvoiceUploadEntity> list);
}
