package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.InvoiceAuthenticationStatisticEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.export.InvoiceAuthenticationStatisticExportExcel;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthenticationSummaryStatisticsService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.google.common.collect.Maps.newHashMap;


/**
 * @author joe.tang
 * @date 2018/4/13
 * 认证发票汇总报表controller
 */
@RestController
public class InvoiceAuthenticationSummaryStatisticsController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceAuthenticationSummaryStatisticsController.class);

    @Autowired
    private InvoiceAuthenticationSummaryStatisticsService invoiceAuthenticationSummaryStatisticsService;

    /**
     * 认证发票统计列表
     *
     * @param params 查询条件
     * @return  认证发票统计数据
     */
    @SysLog("获取认证发票统计")
    @RequestMapping(value = "/report/invoiceauthenticationsummarystatistics/query")
    public R invoiceAuthenticationSummaryStatisticsQuery(@RequestParam Map<String, Object> params) {
        LOGGER.info("获取认证发票统计 invoiceAuthenticationSummaryStatisticsQuery:{}", params);

        //查询列表数据
        params.put("userId", getUserId());
        Query query = new Query(params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<InvoiceAuthenticationStatisticEntity> invoiceList = invoiceAuthenticationSummaryStatisticsService.queryList(query,schemaLabel);
        ReportStatisticsEntity result = invoiceAuthenticationSummaryStatisticsService.queryTotalResult(query,schemaLabel);

        PageUtils pageUtil = new PageUtils(invoiceList, invoiceList.size(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }


    //认证发票统计导出
    @SysLog("发票签收失败统计导出")
    @RequestMapping(value = "/export/invoiceeauthenticationsummarystatistics")
    public void invoiceAuthenticationSummaryStatisticsExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("发票签收失败统计导出 invoiceAuthenticationSummaryStatisticsExport:{}", params, response);

        //查询列表数据
        params.put("userId", getUserId());
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<InvoiceAuthenticationStatisticEntity> invoiceList = invoiceAuthenticationSummaryStatisticsService.queryList(params,schemaLabel);
        final Map<String, Object> map = newHashMap();
        map.put("invoiceauthenticationsummarystatistics", invoiceList);
        map.put("totalAmount", params.get("totalAmount"));
        map.put("totalTax", params.get("totalTax"));
        map.put("totalCount", params.get("totalCount"));
        final InvoiceAuthenticationStatisticExportExcel excel = new InvoiceAuthenticationStatisticExportExcel(map);
        final String excelName = new SimpleDateFormat("yyyyMMdd").format(new Date());
        excel.write(response, "invoiceAuthenticationStatistics" + excelName);
    }


}
