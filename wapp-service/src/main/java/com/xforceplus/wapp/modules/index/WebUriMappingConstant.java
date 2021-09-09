package com.xforceplus.wapp.modules.index;

/**
 * @author Bobby
 * @date 2018/4/16
 */
public final class WebUriMappingConstant {
    private static final String MODULES_ROOT = "/modules/";
    /**
     * 首页-今日发票采集情况
     */
    public static final String URI_INDEX_INVOICE_COLLECTION = MODULES_ROOT + "index/invoice/collection";

    /**
     * 首页-今日发票采集情况
     */
    public static final String URI_INDEX_INVOICE_COLLECTION_SCAN_MATCH = MODULES_ROOT + "index/invoice/collectionScanMatch";


    /**
     * 首页-今日发票采集情况详情
     */
    public static final String URI_INDEX_INVOICE_COLLECTION_DETAIL = MODULES_ROOT + "index/invoice/collection/list";

    /**
     * 首页-今日扫描匹配情况详情
     */
    public static final String URI_INDEX_INVOICE_COLLECTION_DETAIL_MATCH = MODULES_ROOT + "index/invoice/collection/matchlist";

    /**
     * 首页-今日申请已开红票情况详情
     */
    public static final String URI_INDEX_INVOICE_COLLECTION_DETAIL_SQ= MODULES_ROOT + "index/invoice/collection/sqlist";

    /**
     * 首页-今日已开红票情况详情
     */
    public static final String URI_INDEX_INVOICE_COLLECTION_DETAIL_YK= MODULES_ROOT + "index/invoice/collection/yklist";

    /**
     * 首页-今日发票未签收
     */
    public static final String URI_INDEX_INVOICE_COLLECTION_DETAIL_WQS = MODULES_ROOT + "index/invoice/collection/wqslist";


    /**
     * 首页-今日发票采集情况
     */
    public static final String URI_INDEX_INVOICE_SCANNING = MODULES_ROOT + "index/invoice/scanning";

    /**
     * 首页-今日发票采集详情
     */
    public static final String URI_INDEX_INVOICE_SCANNING_DETAIL = MODULES_ROOT + "index/invoice/scanning/list";

    /**
     * 首页-今日发票认证情况
     */
    public static final String URI_INDEX_INVOICE_AUTHENTICATION = MODULES_ROOT + "index/invoice/authentication";

    /**
     * 首页-今日发票认证详情
     */
    public static final String URI_INDEX_INVOICE_AUTHENTICATION_DETAIL = MODULES_ROOT + "index/invoice/authentication/list";

    /**
     * 本月新增发票信息（新增、认证、发票金额（未税）、发票税额）
     */
    public static final String URI_INDEX_INVOICE_INFORMATION_OF_MONTH = MODULES_ROOT + "index/invoice/informationOfMonth";
    /**
     * 首页-待办
     */
    public static final String URI_INDEX_TASK_REMINDING = MODULES_ROOT + "index/invoice/taskReminding";
    /**
     * 首页-本月新增、认证(成功、失败 )图表统计
     */
    public static final String URI_INDEX_CHART_STATISTICS = MODULES_ROOT + "index/invoice/chartStatistics";

    /**
     * 首页-本月签收图表统计
     */
    public static final String URI_INDEX_CHART_STATISTICS_QS = MODULES_ROOT + "index/invoice/chartStatisticsQS";


    /**
     * 首页-本月扫描匹配图表统计
     */
    public static final String URI_INDEX_CHART_STATISTICS_SM = MODULES_ROOT + "index/invoice/chartStatisticsSM";

    /**
     * 首页-本月新增、认证(成功、失败 )图表统计
     */
    public static final String URI_INDEX_CHART_STATISTICS_YEAR = MODULES_ROOT + "index/invoice/chartMonthStatistics";

    /**
     * 首页-本年发票签收图表统计
     */
    public static final String URI_INDEX_CHART_STATISTICS_YEAR_QS = MODULES_ROOT + "index/invoice/chartMonthStatisticsQS";

    /**
     * 首页-本年发票扫描匹配图表统计
     */
    public static final String URI_INDEX_CHART_STATISTICS_YEAR_SM = MODULES_ROOT + "index/invoice/chartMonthStatisticsSM";

    /**
     * 本月已认证发票列表
     */
    public static final String URI_INDEX_CURRENT_MONTH_AUTH = MODULES_ROOT + "index/invoice/currentAuthList";

}
