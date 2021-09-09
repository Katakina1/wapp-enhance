package com.xforceplus.wapp.modules.report.service;

import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.lowagie.text.DocumentException;
import freemarker.template.TemplateException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AuthResultListService {

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
     * 获取数据列表
     * @param map
     * @return
     */
    List<ComprehensiveInvoiceQueryEntity> getList(String schemaLabel,Map<String, Object> map);

    /**
     * 导出PDF
     * @param map
     */
    void exportPdf(Map<String, Object> map, HttpServletResponse response) throws IOException,DocumentException,TemplateException;
}
