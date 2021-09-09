package com.xforceplus.wapp.modules.report.dao;

import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票认证日报
 */
@Mapper
public interface InvoiceAuthDailyReportDao {
    /**
     * 获取日报数据列表
     * @param map
     * @return
     */
    List<DailyReportEntity> getList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 获取纳税人当前税款所属期
     * @param taxno
     * @return
     */
    String getCurrentTaxPeriod(@Param("schemaLabel") String schemaLabel,@Param("taxno") String taxno);
}
