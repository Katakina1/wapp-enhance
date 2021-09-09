package com.xforceplus.wapp.modules.InformationInquiry.dao;


import com.xforceplus.wapp.modules.InformationInquiry.entity.MatchEntity;
import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/14 20:32
 */
@Mapper
public interface DataInvoiceQueryDao {
    /**
     * 数据发票匹配查询
     * @param map
     * @return
     */
    List<MatchEntity> matchlist(@Param("map") Map<String, Object> map);

    /**
     * 数据发票匹配查询
     * @param map
     * @return
     */
    Integer matchlistCount(@Param("map") Map<String, Object> map);

    /**
     * JV下拉选查询
     * @return
     */
    List<GfOptionEntity> searchGf();
}
