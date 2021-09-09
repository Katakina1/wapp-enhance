package com.xforceplus.wapp.modules.report.service;


import com.xforceplus.wapp.modules.report.entity.BatchSystemMatchQueryEntity;
import com.xforceplus.wapp.modules.report.entity.BatchSystemMatchQueryExcelEntity;
import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;

import java.util.List;
import java.util.Map;

public interface BatchSystemMatchQueryService {

    /**
     * 发票处理状态报告信息
     * @param map
     * @return
     */
    List<BatchSystemMatchQueryEntity> matchlists(Map<String, Object> map);
    List<BatchSystemMatchQueryEntity> matchlistAll(Map<String, Object> map);

    /**
     * 发票处理状态报告信息条数
     * @param map
     * @return
     */
    Integer matchlistCounts(Map<String, Object> map);

    /**
     * JV下拉选查询
     * @return
     */
    List<GfOptionEntity> searchGf();

    List<BatchSystemMatchQueryExcelEntity> transformExcle(List<BatchSystemMatchQueryEntity> list);
}
