package com.xforceplus.wapp.modules.index.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.index.entity.IndexInvoiceCollectionModel;
import com.xforceplus.wapp.modules.index.entity.IndexInvoiceScanMatchModel;
import com.xforceplus.wapp.modules.index.entity.IndexInvoiceScanningModel;
import com.xforceplus.wapp.modules.index.service.IndexStatisticsService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.xforceplus.wapp.modules.index.WebUriMappingConstant.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Bobby
 * @date 2018/4/13
 * 首页
 */
@RestController
public class IndexController extends AbstractController {

    private final static Logger LOGGER = getLogger(IndexController.class);

    private IndexStatisticsService indexStatisticsService;


    public IndexController(IndexStatisticsService indexStatisticsService) {
        this.indexStatisticsService = indexStatisticsService;
    }

    /**
     * 今日发票采集情况
     */
    @SysLog("今日发票采集情况")
    @PostMapping(URI_INDEX_INVOICE_COLLECTION)
    public R getInvoiceCollectionSituation(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日发票采集情况,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceCollectionSituationMap(params));
    }

    /**
     * 今日扫描匹配采集情况
     */
    @SysLog("今日扫描匹配采集情况")
    @PostMapping(URI_INDEX_INVOICE_COLLECTION_SCAN_MATCH)
    public R getInvoiceCollectionSituation1(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日扫描匹配采集情况,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceCollectionSituationMap1(params));
    }

    /**
     * 今日发票采集详情表格
     */
    @SysLog("今日发票采集情况")
    @PostMapping(URI_INDEX_INVOICE_COLLECTION_DETAIL)
    public R getInvoiceCollectionSituationList(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日发票采集列表,params {}", params);
        params.put("userId", getUserId());
        final Query query = new Query(params);
        PagedQueryResult<IndexInvoiceCollectionModel> resultList = indexStatisticsService.getInvoiceCollectionSituationList(query);
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }

    /**
     * 今日发票采集详情表格
     */
    @SysLog("今日发票采集情况")
    @PostMapping(URI_INDEX_INVOICE_COLLECTION_DETAIL_MATCH)
    public R getInvoiceCollectionSituationMatchList(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日发票采集列表,params {}", params);
        params.put("userId", getUserId());
        final Query query = new Query(params);
        PagedQueryResult<IndexInvoiceCollectionModel> resultList = indexStatisticsService.getInvoiceCollectionSituationMatchList(query);
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }


    /**
     * 今日申请已开红票情况
     */
    @SysLog("今日申请已开红票情况")
    @PostMapping(URI_INDEX_INVOICE_COLLECTION_DETAIL_SQ)
    public R getInvoiceCollectionSituationSQList(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日申请已开红票列表,params {}", params);
        params.put("userId", getUserId());
        final Query query = new Query(params);
        PagedQueryResult<IndexInvoiceScanMatchModel> resultList = indexStatisticsService.getInvoiceCollectionSituationSQList(query);
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }



    /**
     * 今日已开红票情况
     */
    @SysLog("今日已开红票情况")
    @PostMapping(URI_INDEX_INVOICE_COLLECTION_DETAIL_YK)
    public R getInvoiceCollectionSituationYKList(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日已开红票列表,params {}", params);
        params.put("userId", getUserId());
        final Query query = new Query(params);
        PagedQueryResult<IndexInvoiceScanMatchModel> resultList = indexStatisticsService.getInvoiceCollectionSituationYKList(query);
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }

    /**
     * 今日发票未签收
     */
    @SysLog("今日发票未签收")
    @PostMapping(URI_INDEX_INVOICE_COLLECTION_DETAIL_WQS)
    public R getInvoiceCollectionSituationWqsList(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日发票采集列表,params {}", params);
        params.put("userId", getUserId());
        final Query query = new Query(params);
        PagedQueryResult<IndexInvoiceCollectionModel> resultList = indexStatisticsService.getInvoiceCollectionSituationWqsList( query);
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }

    /**
     * 今日发票扫描情况
     */
    @SysLog("今日发票扫描情况")
    @PostMapping(URI_INDEX_INVOICE_SCANNING)
    public R getInvoiceScanningSituation(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日发票扫描情况,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceScanningSituation(params));
    }

    /**
     * 今日发票扫描详情
     */
    @SysLog("今日发票扫描详情")
    @PostMapping(URI_INDEX_INVOICE_SCANNING_DETAIL)
    public R getInvoiceScanningSituationList(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日发票扫描列表,params {}", params);
        params.put("userId", getUserId());
        final Query query = new Query(params);
        PagedQueryResult<IndexInvoiceScanningModel> resultList = indexStatisticsService.getInvoiceScanningSituationList(query);
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }

    /**
     * 今日发票认证情况
     */
    @SysLog("今日发票认证情况")
    @PostMapping(URI_INDEX_INVOICE_AUTHENTICATION)
    public R getInvoiceAuthenticationSituation(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日发票认证情况,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceAuthenticationSituation(params));
    }


    /**
     * 今日发票认证详情
     */
    @SysLog("今日发票认证详情")
    @PostMapping(URI_INDEX_INVOICE_AUTHENTICATION_DETAIL)
    public R getInvoiceAuthenticationSituationList(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-今日发票认证详情,params {}", params);
        params.put("userId", getUserId());
        final Query query = new Query(params);
        PagedQueryResult<IndexInvoiceCollectionModel> resultList = indexStatisticsService.getInvoiceAuthenticationSituationList( query);
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }

    /**
     * 本月新增发票信息（新增、认证、发票金额（未税）、发票税额）
     */
    @SysLog("本月新增发票信息（新增、认证、发票金额（未税）、发票税额）")
    @PostMapping(URI_INDEX_INVOICE_INFORMATION_OF_MONTH)
    public R getInvoiceInformationOfMonth(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-本月新增发票信息,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceInformationOfMonth(params));
    }

    /**
     * 待办
     */
    @SysLog("待办")
    @PostMapping(URI_INDEX_TASK_REMINDING)
    public R getInvoiceTaskReminding(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-待办,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceTaskReminding(params));
    }

    /**
     * 本月发票新增、认证成功、认证失败图表
     */
    @SysLog("本月发票新增、认证成功、认证失败图表")
    @PostMapping(URI_INDEX_CHART_STATISTICS)
    public R getInvoiceChartStatistics(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-本月发票新增、认证成功、认证失败图表,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceChartStatistics(params));
    }

    /**
     * 本月发票签收图表
     */
    @SysLog("本月发票签收图表")
    @PostMapping(URI_INDEX_CHART_STATISTICS_QS)
    public R getInvoiceChartStatisticsQS(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-本月发票签收图表,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceChartStatisticsQS(params));
    }

    /**
     * 本月发票扫描匹配图表
     */
    @SysLog("本月发票扫描匹配图表")
    @PostMapping(URI_INDEX_CHART_STATISTICS_SM)
    public R getInvoiceChartStatisticsSM(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-本月发票扫描匹配图表,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceChartStatisticsSM(params));
    }

    /**
     * 本年发票新增、认证成功、认证失败图表
     */
    @SysLog("本月发票新增、认证成功、认证失败图表")
    @PostMapping(URI_INDEX_CHART_STATISTICS_YEAR)
    public R getInvoiceChartStatisticsYear(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-本月发票新增、认证成功、认证失败图表,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceChartStatisticsYear(params));
    }

    /**
     * 本年发票签收图表
     */
    @SysLog("本年发票签收图表")
    @PostMapping(URI_INDEX_CHART_STATISTICS_YEAR_QS)
    public R getInvoiceChartStatisticsYearQS(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-本年发票签收图表,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceChartStatisticsYearQS(params));
    }

    /**
     * 本年发票扫描匹配图表
     */
    @SysLog("本年发票扫描匹配图表")
    @PostMapping(URI_INDEX_CHART_STATISTICS_YEAR_SM)
    public R getInvoiceChartStatisticsYearSM(@RequestParam Map<String, Object> params) {
        LOGGER.info("首页-本年发票匹配图表,params {}", params);
        params.put("userId", getUserId());
        return R.ok().put("result", indexStatisticsService.getInvoiceChartStatisticsYearSM(params));
    }


    @SysLog("本月已认证发票列表")
    @PostMapping(URI_INDEX_CURRENT_MONTH_AUTH)
    public R getInvoiceCollectionAuthList(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        PagedQueryResult<IndexInvoiceCollectionModel> resultList = indexStatisticsService.getInvoiceCollectionAuthList( query);
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }
}
