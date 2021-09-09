package com.xforceplus.wapp.modules.index.dao;

import com.xforceplus.wapp.modules.index.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Bobby
 * @date 2018/4/16
 * 首页-数据层
 */
@Mapper
public interface IndexStatisticsMapper {


    /**
     * 今日发票采集情况
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return list集合
     */
    List<IndexInvoiceCollectionCountModel> getInvoiceCollectionSituation(@Param("paramsData") Map<String, Object> params);

    /**
     * 今日发票采集情况
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return list集合
     */
    Map<String, Integer> getInvoiceCollectionSituationMap(@Param("paramsData") Map<String, Object> params);

    /**
     * 今日扫描匹配采集情况
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return list集合
     */
    Map<String, Integer> getInvoiceCollectionSituationMap1(@Param("paramsData") Map<String, Object> params);

    /**
     * 今日发票采集列表
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间，invoiceType:发票类型}
     * @return 结果集合
     */
    List<IndexInvoiceCollectionModel> getInvoiceCollectionSituationList(@Param("paramsData") Map<String, Object> params);

    /**
     * 今日扫描匹配列表
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间
     * @return 结果集合
     */
    List<IndexInvoiceCollectionModel> getInvoiceCollectionSituationMatchList(@Param("paramsData") Map<String, Object> params);

    /**
     * 今日申请已开红票列表
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间
     * @return 结果集合
     */
    List<IndexInvoiceScanMatchModel> getInvoiceCollectionSituationSQList(@Param("paramsData") Map<String, Object> params);

    /**
     * 今日已开红票记录数
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return 结果集合
     */
    Integer getInvoiceCollectionSituationListYKCount( @Param("paramsData") Map<String, Object> params);


    /**
     * 今日已开红票列表
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间
     * @return 结果集合
     */
    List<IndexInvoiceScanMatchModel> getInvoiceCollectionSituationYKList(@Param("paramsData") Map<String, Object> params);

    /**
     * 今日申请已开红票记录数
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return 结果集合
     */
    Integer getInvoiceCollectionSituationListSQCount( @Param("paramsData") Map<String, Object> params);

    /**
     * 今日发票采集列表记录数
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间，invoiceType:发票类型}
     * @return 结果集合
     */
    Integer getInvoiceCollectionSituationListCount( @Param("paramsData") Map<String, Object> params);

    /**
     * 今日扫描匹配记录数
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间}
     * @return 结果集合
     */
    Integer getInvoiceCollectionSituationListMatchCount( @Param("paramsData") Map<String, Object> params);

    /**
     * 今日发票未签收列表
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间，invoiceType:发票类型}
     * @return 结果集合
     */
    List<IndexInvoiceCollectionModel> getInvoiceCollectionSituationWqsList( @Param("paramsData") Map<String, Object> params);

    /**
     * 今日发票未签收列表记录数
     *
     * @param params      {startDate:当天凌晨时间，endDate:当前时间，invoiceType:发票类型}
     * @return 结果集合
     */
    Integer getInvoiceCollectionSituationWqsListCount(@Param("paramsData") Map<String, Object> params);


    /**
     * 今日发票扫描情况
     *
     * @param params{start:当天0点时间，end:当前时间}
     * @return 结果集合
     */
    List<IndexInvoiceScanningCountModel> getInvoiceScanningSituation( @Param("paramsData") Map<String, Object> params);


    /**
     * 今日发票扫描列表
     *
     * @param params{start:当天0点时间，end:当前时间}
     * @return 结果集合
     */
    List<IndexInvoiceScanningModel> getInvoiceScanningSituationList(@Param("paramsData") Map<String, Object> params);

    /**
     * 今日发票扫描列表记录数
     *
     * @param params{start:当天0点时间，end:当前时间}
     * @return 结果集合
     */
    Integer getInvoiceScanningSituationListCount( @Param("paramsData") Map<String, Object> params);


    /**
     * 今日发票认证情况
     *
     * @param params
     * @return
     */
    List<IndexInvoiceAuthenticationCountModel> getInvoiceAuthenticationSituation( @Param("paramsData") Map<String, Object> params);


    /**
     * 今日发票认证列表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceCollectionModel> getInvoiceAuthenticationSituationList( @Param("paramsData") Map<String, Object> params);


    /**
     * 今日发票认证记录数
     *
     * @param params
     * @return
     */
    Integer getInvoiceAuthenticationSituationListCount(@Param("paramsData") Map<String, Object> params);


    /**
     * 本月新增发票信息（新增、认证、发票金额（未税）、发票税额）
     *
     * @param params
     * @return
     */
    List<IndexInvoiceAuthenticationCountModel> getInvoiceInformationOfMonth(@Param("paramsData") Map<String, Object> params);


    /**
     * 本月发票新增、认证成功、认证失败图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatistics(@Param("paramsData") Map<String, Object> params);

    /**
     * 本月发票签收图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsQS(@Param("paramsData") Map<String, Object> params);

    /**
     * 本月发票扫描匹配图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsSM(@Param("paramsData") Map<String, Object> params);

    /**
     * 任务提醒
     *
     * @param params
     * @return
     */
    List<IndexInvoiceTaskRemindingModel> getInvoiceTaskReminding( @Param("paramsData") Map<String, Object> params);

    /**
     * 本年发票新增、认证成功、认证失败图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsYear( @Param("paramsData") Map<String, Object> params);

    /**
     * 本年发票签收图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsYearQS( @Param("paramsData") Map<String, Object> params);

    /**
     * 本年发票扫描匹配图表
     *
     * @param params
     * @return
     */
    List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsYearSM( @Param("paramsData") Map<String, Object> params);

    /**
     * 本月已认证发票数量
     * @param params 参数
     * @return 数量
     */
    Integer getCurrentMonthAuthCount(@Param("paramsData") Map<String, Object> params);

    /**
     * 本月已认证发票列表
     * @param params 参数
     * @return 发票集合
     */
    List<IndexInvoiceCollectionModel> getCurrentMonthAuthList( @Param("paramsData") Map<String, Object> params);
}
