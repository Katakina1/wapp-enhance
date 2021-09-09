package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.export.ComprehensiveInvoiceQueryExcel;
import com.xforceplus.wapp.modules.report.export.ExceptionalInvoiceReportExcel;
import com.xforceplus.wapp.modules.report.export.InputTaxDetailReportExcel;
import com.xforceplus.wapp.modules.report.export.InputTaxReportExcel;
import com.xforceplus.wapp.modules.report.export.InvoiceAuthDailyReportExcel;
import com.xforceplus.wapp.modules.report.export.TimeoutInvoiceReportExcel;
import com.xforceplus.wapp.modules.report.service.AuthResultListService;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import com.xforceplus.wapp.modules.report.service.ExceptionalInvoiceReportService;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthDailyReportService;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthMonthlyReportService;
import com.xforceplus.wapp.modules.report.service.TimeoutInvoiceReportService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 导出专用controller
 */
@RestController
@RequestMapping("/export")
public class ExportController extends AbstractController {

    private final static Logger LOGGER = getLogger(AuthResultListController.class);

    @Autowired
    ComprehensiveInvoiceQueryService comprehensiveInvoiceQueryService;

    @Autowired
    TimeoutInvoiceReportService timeoutInvoiceReportService;

    @Autowired
    ExceptionalInvoiceReportService exceptionalInvoiceReportService;

    @Autowired
    InvoiceAuthDailyReportService invoiceAuthDailyReportService;

    @Autowired
    InvoiceAuthMonthlyReportService invoiceAuthMonthlyReportService;

    @Autowired
    AuthResultListService authResultListService;

    /**
     * 导出数据-发票综合查询
     * @param params
     * @return
     */
    @SysLog("发票综合查询导出")
    @RequestMapping("/comprehensiveInvoiceQueryExport")
    public void comprehensiveInvoiceQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        //查询列表数据
        List<ComprehensiveInvoiceQueryEntity> list = comprehensiveInvoiceQueryService.queryListAll(schemaLabel,params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("comprehensiveInvoiceQueryList", list);
        map.put("totalAmount", params.get("totalAmount"));
        map.put("totalTax", params.get("totalTax"));
        //生成excel
        final ComprehensiveInvoiceQueryExcel excelView = new ComprehensiveInvoiceQueryExcel(map, "export/report/comprehensiveInvoiceQueryList.xlsx", "comprehensiveInvoiceQueryList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "comprehensiveInvoiceQueryList" + excelNameSuffix);
    }

    /**
     * 导出数据-逾期发票报表
     * @param params
     * @return
     */
    @SysLog("逾期发票导出")
    @RequestMapping("/timeoutInvoiceReportExport")
    public void timeoutInvoiceReportExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        //查询列表数据
        List<ComprehensiveInvoiceQueryEntity> list = timeoutInvoiceReportService.queryListAll(schemaLabel,params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("timeoutInvoiceReportList", list);
        map.put("totalAmount", params.get("totalAmount"));
        map.put("totalTax", params.get("totalTax"));
        //生成excel
        final TimeoutInvoiceReportExcel excelView = new TimeoutInvoiceReportExcel(map, "export/report/timeoutInvoiceReportList.xlsx", "timeoutInvoiceReportList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "timeoutInvoiceReportList" + excelNameSuffix);
    }

    /**
     * 导出数据-异常发票报表
     * @param params
     * @return
     */
    @SysLog("异常发票导出")
    @RequestMapping("/exceptionalInvoiceReportExport")
    public void exceptionalInvoiceReportExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        //查询列表数据
        List<ComprehensiveInvoiceQueryEntity> list = exceptionalInvoiceReportService.queryListAll(schemaLabel,params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("exceptionalInvoiceReportList", list);
        map.put("totalAmount", params.get("totalAmount"));
        map.put("totalTax", params.get("totalTax"));
        //生成excel
        final ExceptionalInvoiceReportExcel excelView = new ExceptionalInvoiceReportExcel(map, "export/report/exceptionalInvoiceReportList.xlsx", "exceptionalInvoiceReportList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "exceptionalInvoiceReportList" + excelNameSuffix);
    }

    /**
     * 导出-进项发票报表
     * @param params
     * @return
     */
    @SysLog("进项发票导出")
    @RequestMapping("/inputTaxReportExport")
    public void inputTaxReportExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //生成excel
        final InputTaxReportExcel excelView = new InputTaxReportExcel(params, "export/report/inputTaxReport.xlsx", "inputTaxReport");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "inputTaxReport" + excelNameSuffix);
    }

    /**
     * 导出-进项抵扣税额明细表
     * @param params
     * @return
     */
    @SysLog("进项发票明细导出")
    @RequestMapping("/inputTaxDetailReportExport")
    public void inputTaxDetailReportExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //生成excel
        final InputTaxDetailReportExcel excelView = new InputTaxDetailReportExcel(params, "export/report/inputTaxDetailReport.xlsx", "inputTaxDetailReport");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "inputTaxDetailReport" + excelNameSuffix);
    }

    /**
     * 导出数据-发票认证日报
     * @param params
     * @return
     */
    @SysLog("发票认证日报导出")
    @RequestMapping("/invoiceAuthDailyReportExport")
    public void invoiceAuthDailyReportExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        List<DailyReportEntity> list = invoiceAuthDailyReportService.getList(schemaLabel,params);

        final Map<String, Object> map = newHashMapWithExpectedSize(3);
        map.put("invoiceAuthDailyReportList", list);
        map.put("taxName", params.get("taxName"));
        map.put("taxNo", params.get("taxNo"));
        map.put("totalCount", params.get("totalCount"));
        map.put("totalAmount", params.get("totalAmount"));
        map.put("totalTax", params.get("totalTax"));
        //生成excel
        final InvoiceAuthDailyReportExcel excelView = new InvoiceAuthDailyReportExcel(map, "export/report/invoiceAuthDailyReportList.xlsx", "invoiceAuthDailyReportList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "invoiceAuthDailyReportList" + excelNameSuffix);
    }

    /**
     * 导出数据-发票认证月报
     * @param params
     * @return
     */
    @SysLog("发票认证月报导出")
    @RequestMapping("/invoiceAuthMonthlyReportExport")
    public void invoiceAuthMonthlyReportExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        List<DailyReportEntity> list = invoiceAuthMonthlyReportService.getList(schemaLabel,params);

        final Map<String, Object> map = newHashMapWithExpectedSize(3);
        map.put("invoiceAuthMonthlyReportList", invoiceAuthMonthlyReportService.fixList(list, params.get("rzhBelongDate").toString().substring(0,4)));
        map.put("taxName", params.get("taxName"));
        map.put("taxNo", params.get("taxNo"));
        map.put("totalCount", params.get("totalCount"));
        map.put("totalAmount", params.get("totalAmount"));
        map.put("totalTax", params.get("totalTax"));
        //生成excel
        final InvoiceAuthDailyReportExcel excelView = new InvoiceAuthDailyReportExcel(map, "export/report/invoiceAuthMonthlyReportList.xlsx", "invoiceAuthMonthlyReportList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "invoiceAuthMonthlyReportList" + excelNameSuffix);
    }

    /**
     * 导出数据-认证结果清单
     * @param params
     * @return
     */
    @RequestMapping("/authResultListExport")
    public void authResultListExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();

        if("0".equals(params.get("init"))){
            //刚进页面未查询时,应该没数据
            params.put("resultList", newArrayList());
            params.put("totalData", new ReportStatisticsEntity());
        }else{
            List<ComprehensiveInvoiceQueryEntity> resultList = authResultListService.getList(schemaLabel, params);
            ReportStatisticsEntity totalData = authResultListService.queryTotalResult(schemaLabel, params);
            params.put("resultList", resultList);
            params.put("totalData", totalData);
        }

        params.put("currentDate", new SimpleDateFormat(DEFAULT_SHORT_DATE_FORMAT).format(new Date()));

        try {
            authResultListService.exportPdf(params, response);
        } catch (Exception e){
            LOGGER.error("导出PDF出错:"+e);
        }
    }
}
