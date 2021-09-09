package com.xforceplus.wapp.modules.scanRefund.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.export.EnterPackageNumberPrint;
import com.xforceplus.wapp.modules.scanRefund.export.PrintRefundExcel;
import com.xforceplus.wapp.modules.scanRefund.export.PrintRefundInformationExcel;
import com.xforceplus.wapp.modules.scanRefund.service.EnterPackageNumberService;
import com.xforceplus.wapp.modules.scanRefund.service.PrintRefundInformationService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

/**
 * 发票综合查询
 */
@RestController
public class EnterPackageNumberPrintController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnterPackageNumberPrintController.class);

    @Autowired
    private EnterPackageNumberService enterPackageNumberService;

    /**
     * 退票资料导入
     */
    @SysLog("退票资料导入")
    @PostMapping(value = "/enter/enterPackageNumber")
    public Map<String, Object> getInvoiceCheck(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("退票资料导入,params {}", multipartFile);
        Map<String,Object> map = enterPackageNumberService.parseExcel(multipartFile,getUser().getLoginname());
        return map;
    }
    /**
     * 退票资料模板下载
     */
    @SysLog("退票资料模板下载")
    @RequestMapping(value = "/export/refundEnterPackageNumber")
    public void getInvoiceCheck(HttpServletResponse response) {
        LOGGER.info("导出开红票资料模板,params {}");
        //生成excel
        final EnterPackageNumberPrint excelView = new EnterPackageNumberPrint();
        excelView.write(response, "EnterPackageNumberList");
    }


}
