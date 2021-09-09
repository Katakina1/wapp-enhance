package com.xforceplus.wapp.modules.report.service;

import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;

/**
 * 异常发票报表
 */
public interface ExceptionalInvoiceReportService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<ComprehensiveInvoiceQueryEntity> queryList(String schemaLabel, Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map);

    /**
     * 所有数据列表(不分页)
     * @param map
     * @return
     */
    List<ComprehensiveInvoiceQueryEntity> queryListAll(String schemaLabel,Map<String, Object> map);
}
