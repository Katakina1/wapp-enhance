package com.xforceplus.wapp.modules.report.dao;

import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票认证月报
 */
@Mapper
public interface InvoiceAuthMonthlyReportDao {
    /**
     * 获取月报数据列表
     * @param map
     * @return
     */
    List<DailyReportEntity> getList(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);
}
