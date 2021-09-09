package com.xforceplus.wapp.modules.report.service;

import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface ComprehensiveInvoiceQueryService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<ComprehensiveInvoiceQueryEntity> queryList(String schemaLabel,Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map);

    /**
     * 所有数据列表(不分页)
     * @param map
     * @return
     */
    List<ComprehensiveInvoiceQueryEntity> queryListAll(String schemaLabel,Map<String, Object> map);

    /**
     * 查询购方名称
     * @param userId
     * @return
     */
    List<OptionEntity> searchGf(String schemaLabel,Long userId);

    /**
     * 查询销方名称
     * @param map
     * @return
     */
    List<String> searchXf(String schemaLabel,Map<String, Object> map);
}
