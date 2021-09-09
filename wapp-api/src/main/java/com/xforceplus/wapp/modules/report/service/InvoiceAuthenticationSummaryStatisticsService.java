package com.xforceplus.wapp.modules.report.service;

import com.xforceplus.wapp.modules.report.entity.InvoiceAuthenticationStatisticEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/13
 *认证发票汇总报表Service
 */
public interface InvoiceAuthenticationSummaryStatisticsService {

    /**
     * 获取认证发票汇总数据
     *
     * @param map 查询条件
     * @return 认证发票汇总数据
     */
    List<InvoiceAuthenticationStatisticEntity> queryList(Map<String, Object> map, String schemaLabel);

    /**
     * 获取认证发票汇总金额 税额
     *
     * @param map 询条件
     * @return 认证发票汇总统计
     */
    ReportStatisticsEntity queryTotalResult(Map<String, Object> map, String schemaLabel);

}