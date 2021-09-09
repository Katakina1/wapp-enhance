package com.xforceplus.wapp.modules.fixed.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.fixed.export.InvoiceImportExportExcel;
import com.xforceplus.wapp.modules.fixed.service.InvoiceImportExportService;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
public class InvoiceImportExportController extends AbstractController {
    private static final Logger LOGGER = getLogger(InvoiceImportExportController.class);
    @Autowired
    private InvoiceImportExportService invoiceImportAndExportService;

    @SysLog("发票信息查询")
    @RequestMapping("modules/fixed/invoiceImportExport/list")
    public R getQuestionnaireList(@RequestParam Map<String,Object> params ){
        LOGGER.info("订单信息查询,param{}",params);
        Query query =new Query(params);
        Integer result = invoiceImportAndExportService.invoiceImportAndExportlistCount(query);
        List<InvoiceImportAndExportEntity> list=invoiceImportAndExportService.invoiceImportAndExportlist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }


    @SysLog("导入发票")
    @PostMapping("modules/fixed/invoiceImportExportExportImport")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入发票信息，文件开始导入");
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(5);
        return invoiceImportAndExportService.importInvoice(params,multipartFile);
    }

    /**
     * 导出数据查询
     * password 98156006248284160
     * @param params
     * @return
     */
    @SysLog("查询发票导出")
    @RequestMapping("export/fixed/invoiceImportExport")
    public void signForQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        List<InvoiceImportAndExportEntity> list=invoiceImportAndExportService.invoiceImportAndExportlist(params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("invoiceImportAndExport", list);
        //生成excel
        final InvoiceImportExportExcel excelView = new InvoiceImportExportExcel(map, "export/fixed/invoiceImportAndExport.xlsx", "invoiceImportAndExport");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "invoiceImportAndExport" + excelNameSuffix);
    }
}
