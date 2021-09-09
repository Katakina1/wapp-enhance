package com.xforceplus.wapp.modules.redInvoiceManager.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckModel;
import com.xforceplus.wapp.modules.check.export.CheckTemplate;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import com.xforceplus.wapp.modules.redInvoiceManager.export.RedInvoiceTemplate;
import com.xforceplus.wapp.modules.redInvoiceManager.service.RedInvoiceService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redInvoiceManager.constant.Constants.IMPORT_RED_INVOICE;
import static com.xforceplus.wapp.modules.redInvoiceManager.constant.Constants.RED_INVOICE_TEMPLATE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *  内部红票control类
 */
@RestController
public class RedInvoiceController extends AbstractController {
    private final static Logger LOGGER = getLogger(RedInvoiceController.class);

    @Autowired
    private RedInvoiceService redInvoiceService;



    /**
     * 开红票资料导入
     */
    @SysLog("开红票资料导入")
    @PostMapping(IMPORT_RED_INVOICE)
    public Map<String, Object> getInvoiceCheck(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("开红票资料导入,params {}", multipartFile);
        Map<String,Object> map = redInvoiceService.parseExcel(multipartFile,getUser().getLoginname());
        return map;
    }


    /**
     * 开红票资料模板下载
     */
    @SysLog("开红票资料模板下载")
    @AuthIgnore
    @GetMapping(RED_INVOICE_TEMPLATE)
    public void getInvoiceCheck(HttpServletResponse response) {
        LOGGER.info("导出开红票资料模板,params {}");
        //生成excel
        final RedInvoiceTemplate excelView = new RedInvoiceTemplate();
        excelView.write(response, "RedInvoiceTemplate");
    }

}
