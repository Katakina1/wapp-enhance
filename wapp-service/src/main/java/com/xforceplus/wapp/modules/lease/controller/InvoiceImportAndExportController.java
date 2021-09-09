package com.xforceplus.wapp.modules.lease.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;

import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportExcelEntity;
import com.xforceplus.wapp.modules.lease.export.InvoiceImportAndExportExcel;
import com.xforceplus.wapp.modules.lease.service.InvoiceImportAndExportService;
import com.xforceplus.wapp.modules.report.entity.InvoiceQueryExcelEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * 订单查询
 */
@RestController
public class InvoiceImportAndExportController extends AbstractController {
    private static final Logger LOGGER = getLogger(InvoiceImportAndExportController.class);
    @Autowired
    private InvoiceImportAndExportService invoiceImportAndExportService;


    /**
     * 订单查询
     */
    @SysLog("发票信息查询")
    @RequestMapping("modules/fixed/invoiceImportAndExport/getQuestionnaireList/list")
    public R getQuestionnaireList(@RequestParam Map<String,Object> params ){
        LOGGER.info("订单信息查询,param{}",params);
        Query query =new Query(params);
        Integer result = invoiceImportAndExportService.invoiceImportAndExportlistCount(query);
        List<InvoiceImportAndExportEntity> list=invoiceImportAndExportService.invoiceImportAndExportlist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

//    @SysLog("导出问题单模板")
//    @AuthIgnore
//    @GetMapping("export/InformationInquiry/questionnaireExports")
//    public void exportTemplate(HttpServletResponse response) {
//        LOGGER.info("导出问题单模板");
//
//        //生成excel
//        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/InformationInquiry/questionnaire.xlsx");
//        excelView.write(response, "questionnaire");
//    }


    @SysLog("导入扫描表发票信息")
    @PostMapping("modules/fixed/invoiceImportAndExportExportImport")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入发票信息，文件开始导入");
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(5);
        return invoiceImportAndExportService.importInvoice(params,multipartFile);
    }

    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询扫描表发票导出")
    @RequestMapping("export/fixed/invoiceImportAndExport/invoiceImportAndExportExports")
    public void signForQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        List<InvoiceImportAndExportExcelEntity> list = invoiceImportAndExportService.invoiceImportAndExportlistExcelAll(params);
        try {

            ExcelUtil.writeExcel(response,list,"发票导入导出","sheet1", ExcelTypeEnum.XLSX,InvoiceImportAndExportExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("invoiceImportAndExport", list);
//        //生成excel
//        final InvoiceImportAndExportExcel excelView = new InvoiceImportAndExportExcel(map, "export/lease/invoiceImportAndExport.xlsx", "invoiceImportAndExport");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "invoiceImportAndExport" + excelNameSuffix);
    }
}
