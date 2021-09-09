package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.export.ReceiptInvoiceStatisticsExportExcel;
import com.xforceplus.wapp.modules.report.service.ReceiptInvoiceStatisticsService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.google.common.collect.Maps.newHashMap;


/**
 * @author joe.tang
 * @date 2018/4/12
 * 发票签收统计查询controller
 */
@RestController
public class ReceiptInvoiceStatisticsController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptInvoiceStatisticsController.class);

    @Autowired
    private ReceiptInvoiceStatisticsService receiptInvoiceStatisticsService;

    /**
     * 发票签收统计列表
     *
     * @param params 查询条件
     * @return R
     */
    @SysLog("获取发票签收统计列表")
    @RequestMapping(value = "/report/receiptInvoiceStatistics/query")
    public R receiptInvoiceStatisticsQuery(@RequestParam Map<String, Object> params) {
        LOGGER.info("获取发票签收统计列表 receiptInvoiceStatisticsQuery:{}", params);

        //查询列表数据
        params.put("userId", getUserId());
        Query query = new Query(params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<ComprehensiveInvoiceQueryEntity> invoiceList = receiptInvoiceStatisticsService.queryList(query, schemaLabel);
        int total = receiptInvoiceStatisticsService.queryTotal(query, schemaLabel);
        ReportStatisticsEntity result = receiptInvoiceStatisticsService.queryTotalResult(query, schemaLabel);

        PageUtils pageUtil = new PageUtils(invoiceList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }


    @SysLog("获取发票签收统计列表购方税号")
    @RequestMapping("/report/receiptInvoiceStatistics/searchGf")
    public R searchGf() {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //购方税号
        List<OptionEntity> optionList = receiptInvoiceStatisticsService.searchGf(getUserId(), schemaLabel);

        return R.ok().put("optionList", optionList);
    }

    //发票签收统计导出
    @SysLog("发票签收统计导出")
    @RequestMapping(value = "/export/receiptInvoiceStatistics")
    public void receiptInvoiceExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("发票签收统计导出 receiptInvoiceExport:{}", params, response);

        //查询列表数据
        params.put("userId", getUserId());
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<ComprehensiveInvoiceQueryEntity> invoiceList = receiptInvoiceStatisticsService.queryList(params, schemaLabel);
        final Map<String, Object> map = newHashMap();
        map.put("receiptInvoiceStatistics", invoiceList);
        map.put("totalAmount", params.get("totalAmount"));
        map.put("totalTax", params.get("totalTax"));
        final ReceiptInvoiceStatisticsExportExcel excel = new ReceiptInvoiceStatisticsExportExcel(map);
        final String excelName = new SimpleDateFormat("yyyyMMdd").format(new Date());
        excel.write(response, "receiptInvoiceStatistics" + excelName);
    }

    //发票签收xf
    @SysLog("查询销方名称")
    @RequestMapping("/report/receiptInvoiceStatistics/searchXf")
    public R searchXf(@RequestParam String queryString) {
        Map<String, Object> params = new HashMap<>();
        params.put("queryString", queryString);
        //当前登录人ID,用于查询对应税号
        params.put("userId", getUserId());
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<String> list = receiptInvoiceStatisticsService.searchXf(params, schemaLabel);

        return R.ok().put("list", list);
    }


}
