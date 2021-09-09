package com.xforceplus.wapp.modules.report.service;


import com.xforceplus.wapp.modules.report.entity.InvoiceQuestionQueryExcelEntity;
import com.xforceplus.wapp.modules.report.entity.QuestionInvoiceQuantityAndRatioEntity;

import java.util.List;
import java.util.Map;


public interface SupplierIssueInvoiceQuantityandRatioService {
    /**
     * 查询问题发票数量及比率
     * @param map
     * @return
     */
    List<QuestionInvoiceQuantityAndRatioEntity> problemInvoice(Map<String, Object> map);
    List<InvoiceQuestionQueryExcelEntity> toExcel(List<QuestionInvoiceQuantityAndRatioEntity> list);
}
