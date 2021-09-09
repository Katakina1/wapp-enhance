package com.xforceplus.wapp.modules.report.dao;


import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InvoiceProcessingStatusReportDao {

    /**
     * 发票处理状态报告信息
     * @param map
     * @return
     */
    List<MatchEntity> matchlist(@Param("map") Map<String, Object> map);

    /**
     * 发票处理状态报告信息条数
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
