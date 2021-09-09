package com.xforceplus.wapp.modules.report.dao;

import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.dao.SysBaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 异常发票报表
 */
@Mapper
public interface ExceptionalInvoiceReportDao extends SysBaseDao<ComprehensiveInvoiceQueryEntity> {

    /**
     * 查询所有数据(分页)
     * @param map
     * @return
     */
    List<ComprehensiveInvoiceQueryEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);
    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<ComprehensiveInvoiceQueryEntity> queryListAll(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 查询合计结果
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);
}
