package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.PaymentInvoiceUploadExcel;
import com.xforceplus.wapp.modules.InformationInquiry.export.PaymentInvoiceUploadFailExcel;
import com.xforceplus.wapp.modules.InformationInquiry.export.PaymentInvoiceUploadPrint;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentInvoiceUploadService;
import com.xforceplus.wapp.modules.redInvoiceManager.export.InputRedTicketInformationExport;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InputRedTicketInformationService;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
public class PaymentInvoiceUploadExportController extends AbstractController {

    @Autowired
    private PaymentInvoiceUploadService paymentInvoiceUploadService;

    private static final Logger LOGGER = getLogger(PaymentInvoiceUploadExportController.class);


    /**
     * 扣款发票信息导入
     */
    @SysLog("扣款发票信息导入")
    @PostMapping(value = "/enter/paymentInvoiceUploadPrint1")
    public Map<String, Object> getInvoiceCheck(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("扣款发票信息导入,params {}", multipartFile);
        String  loginName = getUser().getLoginname();
        paymentInvoiceUploadService.delete(loginName);
        Map<String,Object> map = paymentInvoiceUploadService.parseExcel(multipartFile,getUser().getLoginname());
        return map;
    }
    /**
     * 扣款发票模板下载
     */
    @SysLog("扣款发票模板下载")
    @RequestMapping(value = "/export/paymentInvoiceUploadExport")
    public void getInvoiceCheck(HttpServletResponse response) {
        LOGGER.info("导出扣款发票模板,params {}");
        //生成excel
        final PaymentInvoiceUploadPrint excelView = new PaymentInvoiceUploadPrint();
        excelView.write(response, "paymentInvoiceUploadExportList");
    }

    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("问题发票导出")
    @RequestMapping(value = "/export/comprehensiveInvoiceExport1")
    public void comprehensiveInvoiceQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
//        Query query = new Query(params);
//        query.put("userCode",getUser().getUsercode());
        params.put("loginName",getUser().getLoginname());
        List<PaymentInvoiceUploadEntity> list = paymentInvoiceUploadService.queryListAllFail(params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("paymentInvoiceUploadFailList", list);
        //生成excel
        final PaymentInvoiceUploadFailExcel excelView = new PaymentInvoiceUploadFailExcel(map, "export/InformationInquiry/paymentInvoiceUploadFailList.xlsx", "paymentInvoiceUploadFailList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "paymentInvoiceUploadFailList" + excelNameSuffix);
    }


}
