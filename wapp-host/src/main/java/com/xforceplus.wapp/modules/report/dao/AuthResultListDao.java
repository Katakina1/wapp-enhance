package com.xforceplus.wapp.modules.report.dao;

import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 认证结果清单
 */
@Mapper
public interface AuthResultListDao {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<ComprehensiveInvoiceQueryEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 获取数据列表
     * @param map
     * @return
     */
    List<ComprehensiveInvoiceQueryEntity> getList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);
}
