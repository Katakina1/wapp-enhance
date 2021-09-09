package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.export.ReceiptInvoiceFailStatisticsExportExcel;
import com.xforceplus.wapp.modules.report.export.ReceiptInvoiceStatisticsExportExcel;
import com.xforceplus.wapp.modules.report.service.ReceiptInvoiceFailStatisticsService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.joda.time.DateTime.now;

/**
 * @author joe.tang
 * @date 2018/4/14
 * 发票签收失败统计controller
 */
@RestController
public class ReceiptInvoiceFailStatisticsController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptInvoiceFailStatisticsController.class);

    @Autowired
    private ReceiptInvoiceFailStatisticsService receiptInvoiceFailStatisticsService;

    /**
     * 发票签收失败统计列表
     *
     * @param params
     * @return
     */
    @SysLog("获取发票签收失败统计")
    @RequestMapping(value = "/report/receiptInvoiceFailStatistics/query")
    public R receiptInvoiceFailStatistics(@RequestParam Map<String, Object> params) {

        LOGGER.info("获取发票签收失败统计 receiptInvoiceFailStatistics:{}", params);

        //查询列表数据
        params.put("userId", getUserId());
        Query query = new Query(params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<ComprehensiveInvoiceQueryEntity> invoiceList = receiptInvoiceFailStatisticsService.queryList(query, schemaLabel);
        int total = receiptInvoiceFailStatisticsService.queryTotal(query, schemaLabel);
        ReportStatisticsEntity result = receiptInvoiceFailStatisticsService.queryTotalResult(query, schemaLabel);

        PageUtils pageUtil = new PageUtils(invoiceList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }


    @SysLog("获取发票签收失败统计购方税号")
    @RequestMapping("/report/receiptInvoiceFailStatistics/searchGf")
    public R searchGf() {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //购方税号
        List<OptionEntity> optionList = receiptInvoiceFailStatisticsService.searchGf(getUserId(), schemaLabel);

        return R.ok().put("optionList", optionList);
    }


    //发票签收失败统计导出
    @SysLog("发票签收失败统计导出")
    @RequestMapping(value = "/export/receiptInvoiceFailStatistics")
    public void receiptInvoiceFailExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("发票签收失败统计导出 receiptInvoiceFailExport:{}", params, response);

        //查询列表数据
        params.put("userId", getUserId());
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<ComprehensiveInvoiceQueryEntity> invoiceList = receiptInvoiceFailStatisticsService.queryList(params, schemaLabel);
        final Map<String, Object> map = newHashMap();
        map.put("receiptInvoiceFailStatistics", invoiceList);
        map.put("totalAmount", params.get("totalAmount"));
        map.put("totalTax", params.get("totalTax"));
        final ReceiptInvoiceFailStatisticsExportExcel excel = new ReceiptInvoiceFailStatisticsExportExcel(map);
        final String excelName = now().toString("yyyyMMdd");
        excel.write(response, "receiptInvoiceFailStatistics" + excelName);
    }


    //发票签收xf
    @SysLog("查询销方名称")
    @RequestMapping("report/receiptInvoiceFailStatistics/searchXf")
    public R searchXf(@RequestParam String queryString) {
        Map<String, Object> params = new HashMap<>();
        params.put("queryString", queryString);
        //当前登录人ID,用于查询对应税号
        params.put("userId", getUserId());
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<String> list = receiptInvoiceFailStatisticsService.searchXf(params, schemaLabel);

        return R.ok().put("list", list);
    }

}
