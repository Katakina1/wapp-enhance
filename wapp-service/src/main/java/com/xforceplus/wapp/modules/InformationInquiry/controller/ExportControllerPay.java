package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.InformationInquiry.entity.*;
import com.xforceplus.wapp.modules.InformationInquiry.export.*;
import com.xforceplus.wapp.modules.InformationInquiry.service.*;
import com.xforceplus.wapp.modules.InformationInquiry.service.*;
import com.xforceplus.wapp.modules.report.controller.AuthResultListController;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.export.*;
import com.xforceplus.wapp.modules.report.service.*;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;

import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 导出专用controller
 */
@RestController
@RequestMapping("/export")
public class ExportControllerPay extends AbstractController {

    @Autowired
    private PaymentInvoiceUploadService paymentInvoiceUploadService;
    @Autowired
    private PaymentInvoiceQueryService paymentInvoiceQueryService;

    @Autowired
    private PoInquiryService poInquiryService;
    @Autowired
    private ClaimInquiryService claimInquiryService;
    @Autowired
    private ScanningService scanningService;

    @Autowired
    private PaymentDetailService paymentDetailService;
    @Autowired
    private CostListService costListService;

    private static final Logger LOGGER = getLogger(ExportControllerPay.class);

    /**
     * 导出数据查询
     *
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("/comprehensiveInvoiceExport")
    public void comprehensiveInvoiceQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        List<PaymentInvoiceUploadEntity> list = paymentInvoiceUploadService.queryListAll(params);

        //转换Excel数据
        List<PaymentInvoiceUploadExcelEntity> list2=paymentInvoiceUploadService.transformExcle(list);
        try {

            ExcelUtil.writeExcel(response,list2,"发票处理状态报告导出","sheet1", ExcelTypeEnum.XLSX,PaymentInvoiceUploadExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }

//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("paymentInvoiceUploadList", list);
//        //生成excel
//        final PaymentInvoiceUploadExcel excelView = new PaymentInvoiceUploadExcel(map, "export/InformationInquiry/paymentInvoiceUploadList.xlsx", "paymentInvoiceUploadList");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "paymentInvoiceUploadList" + excelNameSuffix);
    }

    @SysLog("订单查询导出")
    @RequestMapping("/InformationInquiryPoExport")
    public void informationInquiryPoExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //查询列表数据
        List<poExcelEntity> list = poInquiryService.selectExcelpolist(params);
        try {
            ExcelUtil.writeExcel(response,list,"订单查询导出","sheet1", ExcelTypeEnum.XLSX,poExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }


//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("polist", list);
//        map.put("sumReturnAmount", params.get("sumReturnAmount"));
//        map.put("amountpaid", params.get("amountpaid"));
//        map.put("amountunpaid", params.get("amountunpaid"));
//        //生成excel
//        final PoInquiryExcel excelView = new PoInquiryExcel(map, "export/InformationInquiry/poquerylist.xlsx", "polist");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "polist" + excelNameSuffix);
    }

    @SysLog("索赔查询导出")
    @RequestMapping("/InformationInquiryClaimExport")
    public void informationInquiryClaimExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //查询列表数据
        List<ClaimExcelEntity> list = claimInquiryService.selectExcelClaimlist(params);
        try {
            ExcelUtil.writeExcel(response,list,"索赔查询导出","sheet1", ExcelTypeEnum.XLSX,ClaimExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("claimlist", list);
//        map.put("claimAmount",params.get("claimAmount"));
//        //生成excel
//        final ClaimInquiryExcel excelView = new ClaimInquiryExcel(map, "export/InformationInquiry/claimqueryList.xlsx", "claimlist");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "claimlist" + excelNameSuffix);
    }

    @SysLog("GF供应商付款明细查询导出")
    @RequestMapping("/InformationInquiryPaymentExportGF")
    public void informationInquiryPaymentExportGF(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //查询列表数据
        List<PaymentDetailExcelEntity1> list = paymentDetailService.selectFindPayListGF(params);
        try {
            ExcelUtil.writeExcel(response,list,"供应商付款明细信息","sheet1", ExcelTypeEnum.XLSX,PaymentDetailExcelEntity1.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        logger.debug("导出:" + list);
//
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("paymentlist", list);
//        //生成excel
//        final PaymentDetailExcel excelView = new PaymentDetailExcel(map, "export/InformationInquiry/paymentlist.xlsx", "paymentlist");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "paymentlist" + excelNameSuffix);
    }

    @SysLog("XF供应商付款明细查询导出")
    @RequestMapping("/InformationInquiryPaymentExport")
    public void informationInquiryPaymentExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //查询列表数据
        List<PaymentDetailExcelEntity> list = paymentDetailService.selectFindPayList(params);
        try {
            ExcelUtil.writeExcel(response,list,"供应商付款明细信息","sheet1", ExcelTypeEnum.XLSX,PaymentDetailExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        logger.debug("导出:" + list);
//
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("paymentlist", list);
//        //生成excel
//        final PaymentDetailExcel excelView = new PaymentDetailExcel(map, "export/InformationInquiry/paymentlist.xlsx", "paymentlist");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "paymentlist" + excelNameSuffix);
    }

    /**
     * 扣款发票查询导出(销)
     *
     * @param params
     * @return
     */
    @SysLog("扣款发票查询导出")
    @RequestMapping("/paymentInvoiceExport")
    public void paymentInvoiceExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        Query query = new Query(params);
        query.put("userCode", getUser().getUsercode());
        List<PaymentInvoiceUploadEntity> list = paymentInvoiceQueryService.queryListAll(query);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("paymentInvoiceUploadList", list);
        //生成excel
        final PaymentInvoiceUploadExcel excelView = new PaymentInvoiceUploadExcel(map, "export/InformationInquiry/paymentInvoiceUploadList.xlsx", "paymentInvoiceUploadList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "paymentInvoiceUploadList" + excelNameSuffix);
    }

    @SysLog("问题单查询导出(商品)")
    @RequestMapping("/InformationInquiryScanningExport")
    public void informationInquiryScanningExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //查询列表数据
        List<ScanningExcelEntity> list = scanningService.selectScanningList(params);
        try {
            ExcelUtil.writeExcel(response,list,"问题单信息（商品）","sheet1", ExcelTypeEnum.XLSX,ScanningExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("scanninglist", list);
//        //生成excel
//        final ScanningExcel excelView = new ScanningExcel(map, "export/InformationInquiry/scanningList.xlsx", "scanninglist");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "scanninglist" + excelNameSuffix);
    }

    @SysLog("问题单查询导出(费用)")
    @RequestMapping("/InformationInquiryCostListExport")
    public void InformationInquiryCostListExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        //查询列表数据
        List<ScanningEntity> list = costListService.scanningList(params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("costlist", list);
        //生成excel
        final CostListExcel excelView = new CostListExcel(map, "export/InformationInquiry/costList.xlsx", "costlist");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "costlist" + excelNameSuffix);
    }
}
