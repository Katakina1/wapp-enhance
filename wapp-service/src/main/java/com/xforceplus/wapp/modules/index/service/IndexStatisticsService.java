package com.xforceplus.wapp.modules.index.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.index.entity.*;

import java.util.List;
import java.util.Map;

/**
 * @author Bobby
 * @date 2018/4/16
 * 首页-业务层
 */

public interface IndexStatisticsService {


    /**
     * 今日发票采集情况
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return list集合
     */
    List<IndexInvoiceCollectionCountModel> getInvoiceCollectionSituation( Map<String, Object> params);

    /**
     * 今日发票采集情况
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return list集合
     */
    List<IndexInvoiceCollectionCountModel> getInvoiceCollectionSituationMap(Map<String, Object> params);

    /**
     * 今日扫描匹配采集情况
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return list集合
     */
    List<IndexInvoiceScanMatchCountModel> getInvoiceCollectionSituationMap1(Map<String, Object> params);

    /**
     * 今日发票采集列表
     *
     * @param params      {invoiceType:发票类型}
     * @return 结果集合
     */
    PagedQueryResult<IndexInvoiceCollectionModel> getInvoiceCollectionSituationList( Map<String, Object> params);

    /**
     * 今日发票采集列表
     *
     * @param params
     * @return 结果集合
     */
    PagedQueryResult<IndexInvoiceCollectionModel> getInvoiceCollectionSituationMatchList( Map<String, Object> params);

    /**
     * 今日申请已开红票列表
     *
     * @param params
     * @return 结果集合
     */
    PagedQueryResult<IndexInvoiceScanMatchModel> getInvoiceCollectionSituationSQList( Map<String, Object> params);

    /**
     * 今日已开红票列表
     *
     * @param params
     * @return 结果集合
     */
    PagedQueryResult<IndexInvoiceScanMatchModel> getInvoiceCollectionSituationYKList( Map<String, Object> params);

    /**
     * 今日发票未签收
     *
     * @param params      {invoiceType:发票类型}
     * @return 结果集合
     */
    PagedQueryResult<IndexInvoiceCollectionModel> getInvoiceCollectionSituationWqsList(Map<String, Object> params);


    /**
     * 今日发票扫描情况
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return
     */
    List<IndexInvoiceScanningCountModel> getInvoiceScanningSituation(Map<String, Object> params);


    /**
     * 今日发票扫描列表
     *
     * @param params      {start:当天0点时间，end:当前时间}
     * @return
     */
    PagedQueryResult<IndexInvoiceScanningModel> getInvoiceScanningSituationList(Map<String, Object> params);


    /**
     * 今日发票认证情况
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return
     */
    List<IndexInvoiceAuthenticationCountModel> getInvoiceAuthenticationSituation( Map<String, Object> params);


    /**
     * 今日发票认证列表
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return
     */
    PagedQueryResult<IndexInvoiceCollectionModel> getInvoiceAuthenticationSituationList( Map<String, Object> params);

    /**
     * 本月新增发票信息（新增、认证、发票金额（未税）、发票税额）
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return
     */
    List<IndexInvoiceAuthenticationCountModel> getInvoiceInformationOfMonth( Map<String, Object> params);


    /**
     * 本月发票新增、认证成功、认证失败图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatistics( Map<String, Object> params);

    /**
     * 本月发票签收图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsQS( Map<String, Object> params);

    /**
     * 本月发票扫描匹配图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsSM( Map<String, Object> params);

    /**
     * 本月发票新增、认证成功、认证失败图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceTaskRemindingModel> getInvoiceTaskReminding( Map<String, Object> params);

    /**
     * 本月发票新增、认证成功、认证失败图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsYear(Map<String, Object> params);

    /**
     * 本年发票签收图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsYearQS(Map<String, Object> params);

    /**
     * 本年发票扫描匹配图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsYearSM(Map<String, Object> params);


    /**
     * 本月认证发票列表
     *
     * @param params     参数
     * @return 结果集合
     */
    PagedQueryResult<IndexInvoiceCollectionModel> getInvoiceCollectionAuthList( Map<String, Object> params);
}
