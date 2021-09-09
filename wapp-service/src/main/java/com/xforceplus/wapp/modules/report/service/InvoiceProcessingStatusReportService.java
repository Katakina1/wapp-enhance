package com.xforceplus.wapp.modules.report.service;


import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
import com.xforceplus.wapp.modules.report.entity.MatchExcelEntity;

import java.util.List;
import java.util.Map;

public interface InvoiceProcessingStatusReportService {

    /**
     * 发票处理状态报告信息
     * @param map
     * @return
     */
    List<MatchEntity> matchlist(Map<String, Object> map);

    /**
     * 发票处理状态报告信息条数
     * @param map
     * @return
     */
    Integer matchlistCount(Map<String, Object> map);

    /**
     * JV下拉选查询
     * @return
     */
    List<GfOptionEntity> searchGf();

    List<MatchExcelEntity> transformExcle(List<MatchEntity> list);
}
