package com.xforceplus.wapp.modules.InformationInquiry.service;



import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface PaymentInvoiceQueryService {

    /**
     * 分页列表
     * @param map
     * @return
     */
    List<PaymentInvoiceUploadEntity> queryList(Map<String, Object> map);

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
    List<PaymentInvoiceUploadEntity> queryListAll(Map<String, Object> map);

    List<PaymentInvoiceUploadExcelEntity> transformExcle(List<PaymentInvoiceUploadEntity> invoiceEntityList);
}
