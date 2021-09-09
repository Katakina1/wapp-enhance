package com.xforceplus.wapp.modules.report.service;

import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/14
 * 签收未认证统计
 */
public interface SignedUncertifiedService {
    /**
     * 获取签收未认证统计数据
     *
     * @param map 查询条件
     * @return 签收未认证统计数据
     */
    List<ComprehensiveInvoiceQueryEntity> queryList(Map<String, Object> map, String schemaLabel);

    /**
     * 获取签收未认证统计的数量
     *
     * @param map 查询条件
     * @return 签收未认证统计数量
     */
    int queryTotal(Map<String, Object> map, String schemaLabel);


    /**
     * 获取签收未认证统计金额 税额
     *
     * @param map 询条件
     * @return 发票签收统计
     */
    ReportStatisticsEntity queryTotalResult(Map<String, Object> map, String schemaLabel);

    /**
     * 查询购方名称
     *
     * @param userId 登陆者id
     * @return List<OptionEntity>
     */
    List<OptionEntity> searchGf(Long userId, String schemaLabel);

    /**
     * 查询销方名称
     *
     * @param map
     * @return
     */
    List<String> searchXf(Map<String, Object> map, String schemaLabel);
}
