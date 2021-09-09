package com.xforceplus.wapp.modules.InformationInquiry.service;



import com.xforceplus.wapp.modules.InformationInquiry.entity.MatchEntity;
import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/14 20:26
 */
public interface DataInvoiceQueryService {
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
