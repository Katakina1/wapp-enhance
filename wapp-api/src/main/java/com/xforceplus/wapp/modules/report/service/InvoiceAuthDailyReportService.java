package com.xforceplus.wapp.modules.report.service;

import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;

import java.util.List;
import java.util.Map;

public interface InvoiceAuthDailyReportService {
    /**
     * 获取日报数据列表
     * @param map
     * @return
     */
    List<DailyReportEntity> getList(String schemaLabel, Map<String, Object> map);

    /**
     * 获取纳税人当前税款所属期
     * @param taxno
     * @return
     */
    String getCurrentTaxPeriod(String schemaLabel,String taxno);
}
