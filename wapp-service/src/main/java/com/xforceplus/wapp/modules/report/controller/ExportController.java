package com.xforceplus.wapp.modules.report.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poExcelEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolExcelEntity;
import com.xforceplus.wapp.modules.report.entity.*;
import com.xforceplus.wapp.modules.report.export.*;
import com.xforceplus.wapp.modules.report.service.*;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.modules.transferOut.entity.DetailEntity;
import com.xforceplus.wapp.modules.transferOut.service.DetailService;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
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

    @Autowired
    private DetailService detailService;

    @Autowired
    private InvoiceProcessingStatusReportService invoiceProcessingStatusReportService;

    @Autowired
    private BatchSystemMatchQueryService batchSystemMatchQueryService;

    @Autowired
    private SupplierIssueInvoiceQuantityandRatioService supplierIssueInvoiceQuantityandRatioService;

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
        params.put("xfName", URLDecoder.decode((String)params.get("xfName")));
        //查询列表数据
        List<ComprehensiveInvoiceQueryExcelEntity> list = comprehensiveInvoiceQueryService.queryExcelListAll(schemaLabel,params);
        try {
            ExcelUtil.writeExcel(response,list,"发票综合查询导出","sheet1", ExcelTypeEnum.XLSX,ComprehensiveInvoiceQueryExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//     ;
//        map.put("totalAmount", params.get("totalAmount"));
//        map.put("totalTax", params.get("totalTax"));
//        //生成excel
//        String excelName="comprehensiveInvoiceQueryList";
//        ComprehensiveInvoiceQueryExcel excelView=null;
//        if((String)params.get("qbfpcx")!=null && ("1").equals((String)params.get("qbfpcx"))){
//            map.put("invoiceExport", list);
//              excelView = new ComprehensiveInvoiceQueryExcel(map, "export/report/invoiceExport.xlsx", "invoiceExport");
//            excelName="invoiceQuery";
//        }else {
//            map.put("taxAmount",params.get("taxAmount"));
//            map.put("comprehensiveInvoiceQueryList", list);
//              excelView = new ComprehensiveInvoiceQueryExcel(map, "export/report/comprehensiveInvoiceQueryList.xlsx", "comprehensiveInvoiceQueryList");
//
//        }
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, excelName + excelNameSuffix);
    }


    /**
     * 导出数据-发票综合查询导出明细
     * @param params
     * @return
     */
    @SysLog("发票综合查询导出明细")
    @RequestMapping("/comprehensiveInvoiceQueryExportMX")
    public void comprehensiveInvoiceQueryExportMX(@RequestParam Map<String, Object> params, HttpServletResponse response, HttpServletRequest request) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        params.put("xfName", URLDecoder.decode((String)params.get("xfName")));
        //查询列表数据
        List<ComprehensiveInvoiceQueryEntity> list = comprehensiveInvoiceQueryService.queryListAll(schemaLabel,params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        Map<Long, List<DetailEntity>> map2 = new HashMap<>();
        //需要优化
        for(ComprehensiveInvoiceQueryEntity comprehensiveInvoiceQueryEntity:list){
            List<DetailEntity> outList = detailService.getInvoiceDetailByUUID(schemaLabel, comprehensiveInvoiceQueryEntity.getUuid());
            map2.put(comprehensiveInvoiceQueryEntity.getId(),outList);
        }


        map.put("comprehensiveInvoiceQueryListMX", map2);
        map.put("comprehensiveInvoiceQueryList", list);
        map.put("totalAmount", params.get("totalAmount"));
        map.put("totalTax", params.get("totalTax"));
        //生成excel
        final ComprehensiveInvoiceQueryExcelMX excelView = new ComprehensiveInvoiceQueryExcelMX(map, "export/report/comprehensiveInvoiceQueryListMX.xlsx", "comprehensiveInvoiceQueryListMX");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        try {
            excelView.write2(response, "comprehensiveInvoiceQueryListMX","comprehensiveInvoiceQueryList",request,excelNameSuffix);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出数据-发票综合查询导出税率
     * @param params
     * @return
     */
    @SysLog("发票综合查询导出税率")
    @RequestMapping("/comprehensiveInvoiceQueryExportSL")
    public void comprehensiveInvoiceQueryExportSL(@RequestParam Map<String, Object> params, HttpServletResponse response, HttpServletRequest request) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        params.put("xfName", URLDecoder.decode((String)params.get("xfName")));
        //查询列表数据
        List<ComprehensiveInvoiceQueryEntity> invoiceSL = comprehensiveInvoiceQueryService.queryListSL(schemaLabel,params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);

        map.put("comprehensiveInvoiceQueryListSL", invoiceSL);

        //生成excel
        final ComprehensiveInvoiceQueryExcelSL excelView = new ComprehensiveInvoiceQueryExcelSL(map, "export/report/comprehensiveInvoiceQueryListSL.xlsx", "comprehensiveInvoiceQueryListSL");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "comprehensiveInvoiceQueryListSL" + excelNameSuffix);
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

        try {
            String encodedGfName = (String)params.get("taxName");
            encodedGfName = URLDecoder.decode(encodedGfName, "GBK");
            params.put("taxName", encodedGfName);
        } catch (Exception e){
            LOGGER.error("导出出错:"+e);
        }

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

        try {
            String encodedGfName = (String)params.get("taxName");
            encodedGfName = URLDecoder.decode(encodedGfName, "GBK");
            params.put("taxName", encodedGfName);
        } catch (Exception e){
            LOGGER.error("导出出错:"+e);
        }

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


        params.put("currentDate", now().toString("yyyy-MM-dd"));

        try {
            String encodedGfName = (String)params.get("gfName");
            encodedGfName = URLDecoder.decode(encodedGfName, "GBK");
            params.put("gfName", encodedGfName);
            authResultListService.exportPdf(params, response);
        } catch (Exception e){
            LOGGER.error("导出PDF出错:"+e);
        }
    }

    @SysLog("发票处理状态报告导出")
    @RequestMapping("/InvoiceProcessingStatusReportExport")
    public void informationInquiryClaimExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //查询列表数据
        List<MatchEntity> list=invoiceProcessingStatusReportService.matchlist(params);
        //转换Excel数据
        List<MatchExcelEntity> list2=invoiceProcessingStatusReportService.transformExcle(list);
        try {

            ExcelUtil.writeExcel(response,list2,"发票处理状态报告导出","sheet1", ExcelTypeEnum.XLSX,MatchExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }

//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("matchlist", list);
//        //生成excel
//        final InvoiceProcessingStatusReportExcel excelView = new InvoiceProcessingStatusReportExcel(map, "export/report/matchList.xlsx", "matchlist");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "matchlist" + excelNameSuffix);
    }

    @SysLog("发票处理状态报告导出")
    @RequestMapping("/SupplierIssueInvoiceQuantityandRatioExport")
    public void supplierIssueInvoiceQuantityandRatioExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //查询列表数据
        List<QuestionInvoiceQuantityAndRatioEntity> list=supplierIssueInvoiceQuantityandRatioService.problemInvoice(params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("problemInvoicelist", list);
        //生成excel
        final SupplierIssueInvoiceQuantityandRatioExcel excelView = new SupplierIssueInvoiceQuantityandRatioExcel(map, "export/report/problemInvoiceList.xlsx", "problemInvoicelist");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "problemInvoicelist" + excelNameSuffix);
    }



    @SysLog("批量导入匹配报告导出")
    @RequestMapping("/InvoiceProcessingStatusReportExports")
    public void informationInquiryClaimExports(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //查询列表数据
        List<BatchSystemMatchQueryEntity> list= batchSystemMatchQueryService.matchlistAll(params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("batchSystemMatchQuery", list);
        //生成excel
        final BatchSystemMatchQueryExcel excelView = new BatchSystemMatchQueryExcel(map, "export/report/batchSystemMatchQuery.xlsx", "batchSystemMatchQuery");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "batchSystemMatchQuery" + excelNameSuffix);
    }

    /**
     * 导出数据-发票查询
     * @param params
     * @return
     */
    @SysLog("发票查询导出")
    @RequestMapping("/invoiceQueryExport")
    public void invoiceQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        params.put("xfName", URLDecoder.decode((String)params.get("xfName")));
        String venderno = ""+params.get("venderno");
        if("null".equals(venderno)){
            params.put("venderno",null);
        }
        //查询列表数据
        List<InvoiceQueryExcelEntity> list = comprehensiveInvoiceQueryService.queryInvoiceExcelListAll(schemaLabel,params);
        try {
            ExcelUtil.writeExcel(response,list,"发票查询导出","sheet1", ExcelTypeEnum.XLSX,InvoiceQueryExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//     ;
//        map.put("totalAmount", params.get("totalAmount"));
//        map.put("totalTax", params.get("totalTax"));
//        //生成excel
//        String excelName="comprehensiveInvoiceQueryList";
//        ComprehensiveInvoiceQueryExcel excelView=null;
//        if((String)params.get("qbfpcx")!=null && ("1").equals((String)params.get("qbfpcx"))){
//            map.put("invoiceExport", list);
//              excelView = new ComprehensiveInvoiceQueryExcel(map, "export/report/invoiceExport.xlsx", "invoiceExport");
//            excelName="invoiceQuery";
//        }else {
//            map.put("taxAmount",params.get("taxAmount"));
//            map.put("comprehensiveInvoiceQueryList", list);
//              excelView = new ComprehensiveInvoiceQueryExcel(map, "export/report/comprehensiveInvoiceQueryList.xlsx", "comprehensiveInvoiceQueryList");
//
//        }
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, excelName + excelNameSuffix);
    }
}
