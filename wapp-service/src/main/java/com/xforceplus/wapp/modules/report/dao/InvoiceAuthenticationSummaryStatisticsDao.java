package com.xforceplus.wapp.modules.report.dao;

import com.xforceplus.wapp.modules.report.entity.InvoiceAuthenticationStatisticEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/13
 * 认证发票汇总统计
 */
@Mapper
public interface InvoiceAuthenticationSummaryStatisticsDao {

    /**
     * 获取认证发票汇总数据
     *
     * @param map 查询条件
     * @return 认证发票汇总数据
     */
    List<InvoiceAuthenticationStatisticEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 获取认证发票汇总金额 税额
     *
     * @param map 查询条件
     * @return 认证发票汇总统计
     */
    ReportStatisticsEntity queryTotalResult(@Param("condition") Map<String, Object> map, @Param("schemaLabel") String schemaLabel);


}
