package com.xforceplus.wapp.modules.report.dao;

import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.InvoiceAuthenticationStatisticEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/12
 * 发票签收统计查询
 */
@Mapper
public interface ReceiptInvoiceStatisticsDao {

    /**
     * 获取发票签收统计数据
     *
     * @param map 查询条件
     * @return 发票签收统计数据
     */
    List<ComprehensiveInvoiceQueryEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 获取发票签收统计数据的数量
     *
     * @param map 查询条件
     * @return 发票签收统计数据数量
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 获取签收发票统计金额 税额
     *
     * @param map 查询条件
     * @return 发票签收统计
     */
    ReportStatisticsEntity queryTotalResult(@Param("condition") Map<String, Object> map, @Param("schemaLabel") String schemaLabel);


    /**
     * 查询购方名称
     *
     * @param userId 登陆者id
     * @return List<OptionEntity>
     */
    List<OptionEntity> searchGf(@Param("userId") Long userId, @Param("schemaLabel") String schemaLabel);


    /**
     * 查询销方名称
     *
     * @param map 查询条件
     * @return 销方名称
     */
    List<String> searchXf(@Param("condition") Map<String, Object> map, @Param("schemaLabel") String schemaLabel);



}
