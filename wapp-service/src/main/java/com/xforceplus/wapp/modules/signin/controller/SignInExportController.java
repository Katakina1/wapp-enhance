package com.xforceplus.wapp.modules.signin.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireExcelEntity;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
import com.xforceplus.wapp.modules.report.export.InqueryInvoiceMRYXExcel;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceExcelEntity;
import com.xforceplus.wapp.modules.signin.service.*;
import com.xforceplus.wapp.modules.signin.toexcel.InqueryInvoiceCostExcel;
import com.xforceplus.wapp.modules.signin.toexcel.InqueryInvoiceExcel;
import com.xforceplus.wapp.modules.signin.toexcel.ReceiptInvoiceExcel;
import com.xforceplus.wapp.modules.signin.toexcel.SignatureProcessingExcel;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;

/**
 * 签收导出专用
 * CreateBy leal.liang on 2018/4/18.
 **/
@RestController
@RequestMapping("/export")
public class SignInExportController extends AbstractController {

    private HandWorkService handWorkService;

    private PhoneAppSignInService phoneAppSignInService;

    private SignInInqueryService signInInqueryService;
    private SignInInqueryMRXYService signInInqueryMRXYService;

    private SignatureProcessingService signatureProcessingService;
    @Autowired
    private SignInInqueryCostService signInInqueryCostService;

    @Autowired
    public SignInExportController(SignInInqueryMRXYService signInInqueryMRXYService,HandWorkService handWorkService, PhoneAppSignInService phoneAppSignInService, SignInInqueryService signInInqueryService, SignatureProcessingService signatureProcessingService) {
        this.handWorkService = handWorkService;
        this.phoneAppSignInService = phoneAppSignInService;
        this.signInInqueryService = signInInqueryService;
        this.signatureProcessingService = signatureProcessingService;
        this.signInInqueryMRXYService=signInInqueryMRXYService;
    }


    @SysLog("发票签收-手工签收导出列表")
    @RequestMapping("/handworkdataexport")
    public void informationExport(@RequestParam  Map<String, Object> params, HttpServletResponse response) {
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        final List<RecordInvoiceEntity> InvoiceEntityList = handWorkService.queryList(schemaLabel,params);
        final Map<String, List<RecordInvoiceEntity>> viewMap = newHashMap();
        viewMap.put("InvoiceEntityList", InvoiceEntityList);
        final ReceiptInvoiceExcel excel = new ReceiptInvoiceExcel(viewMap,"手工签收");
        excel.write(response);

    }

    @SysLog("发票签收-app签收导出列表")
    @RequestMapping("/phoneappdataexport")
    public void appDateExport(@RequestParam  Map<String, Object> params, HttpServletResponse response) {
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        final List<RecordInvoiceEntity> InvoiceEntityList = phoneAppSignInService.queryAllList(schemaLabel,params);
        final Map<String, List<RecordInvoiceEntity>> viewMap = newHashMap();
        viewMap.put("InvoiceEntityList", InvoiceEntityList);
        final ReceiptInvoiceExcel excel = new ReceiptInvoiceExcel(viewMap, "手机APP签收");
        excel.write(response);

    }

    @SysLog("发票签收-签收查询导出列表")
    @RequestMapping("/inqueryDataExport")
    public void inqueryDataExport(@RequestParam  Map<String, Object> params, HttpServletResponse response){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        final List<RecordInvoiceEntity> InvoiceEntityList = signInInqueryService.queryAllList(schemaLabel,params);
        final Map<String, List<RecordInvoiceEntity>> viewMap = newHashMap();
        viewMap.put("InvoiceEntityList", InvoiceEntityList);

        List<RecordInvoiceExcelEntity> list2=signInInqueryService.transformExcle(InvoiceEntityList);
        try {

            ExcelUtil.writeExcel(response,list2,"扫描处理导出","sheet1", ExcelTypeEnum.XLSX,RecordInvoiceExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }


//
//        final InqueryInvoiceExcel excel = new InqueryInvoiceExcel(viewMap);
//        excel.write(response);
    }
    
    @SysLog("发票签收-签收查询导出列表")
    @RequestMapping("/inqueryDataCostExport")
    public void inqueryDataCostExport(@RequestParam  Map<String, Object> params, HttpServletResponse response){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        final List<RecordInvoiceEntity> InvoiceEntityList = signInInqueryCostService.queryAllList(schemaLabel,params);
        final Map<String, List<RecordInvoiceEntity>> viewMap = newHashMap();
        viewMap.put("InvoiceEntityList", InvoiceEntityList);
        final InqueryInvoiceCostExcel excel = new InqueryInvoiceCostExcel(viewMap);
        excel.write(response);
    }

    @SysLog("发票签收-签收查询导出列表")
    @RequestMapping("/inqueryDataExportMRXY")
    public void inqueryDataExportMRXY(@RequestParam  Map<String, Object> params, HttpServletResponse response){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        final List<RecordInvoiceEntity> InvoiceEntityList = signInInqueryMRXYService.queryAllList(schemaLabel,params);
        final Map<String, List<RecordInvoiceEntity>> viewMap = newHashMap();
        viewMap.put("InvoiceEntityList", InvoiceEntityList);
        final InqueryInvoiceMRYXExcel excel = new InqueryInvoiceMRYXExcel(viewMap);
        excel.write(response);
    }

    @SysLog("发票签收-签收处理导出列表")
    @RequestMapping("/SignatureProcessingDateExport")
    public void  signatureProcessingExport(@RequestParam  Map<String, Object> params, HttpServletResponse response){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        final List<RecordInvoiceEntity> InvoiceEntityList = signatureProcessingService.queryAllList(schemaLabel,params);
        final Map<String, List<RecordInvoiceEntity>> viewMap = newHashMap();
        viewMap.put("InvoiceEntityList", InvoiceEntityList);
        final SignatureProcessingExcel excel = new SignatureProcessingExcel(viewMap);
        excel.write(response);
    }

    @SysLog("导出签收模板")
    @AuthIgnore
    @GetMapping("/exportSignTemp")
    public void exportTemplate(HttpServletResponse response) {
        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/signin/signTemplate.xlsx");
        excelView.write(response, "signTemplate");
    }
}
