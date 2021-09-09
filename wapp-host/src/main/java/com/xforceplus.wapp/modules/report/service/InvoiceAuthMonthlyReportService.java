package com.xforceplus.wapp.modules.report.service;

import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface InvoiceAuthMonthlyReportService {
    /**
     * 获取月报数据列表
     * @param map
     * @return
     */
    List<DailyReportEntity> getList(String schemaLabel,Map<String, Object> map);

    /**
     * 修正月报数据,使得列表上没有缺失月份
     * @param list
     * @param year
     * @return
     */
    List<DailyReportEntity> fixList(List<DailyReportEntity> list, String year);
}
