package com.xforceplus.wapp.modules.report.dao;


import com.xforceplus.wapp.modules.report.entity.BatchSystemMatchQueryEntity;
import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BatchSystemMatchQueryDao {

    /**
     * 系统匹配查询
     * @param map
     * @return
     */
    List<BatchSystemMatchQueryEntity> matchlists(@Param("map") Map<String, Object> map);

    List<BatchSystemMatchQueryEntity> matchlistAll(@Param("map") Map<String, Object> map);

    /**
     * 系统匹配查询条数
     * @param map
     * @return
     */
    Integer matchlistCounts(@Param("map") Map<String, Object> map);

    /**
     * JV下拉选查询
     * @return
     */
    List<GfOptionEntity> searchGf();
}
