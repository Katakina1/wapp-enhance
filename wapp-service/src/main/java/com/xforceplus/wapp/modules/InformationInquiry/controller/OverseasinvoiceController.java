package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.OverseasInvoiceEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.OverseasinvoiceService;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/InformationInquiry/OverseasInvoice")
public class OverseasinvoiceController extends AbstractController {
    private static final Logger LOGGER = getLogger(OverseasinvoiceController.class);
    @Autowired
    private OverseasinvoiceService overseasinvoiceService;

    @SysLog("海外发票查询")
    @RequestMapping("/list")
    public R getReturnGoodsList(@RequestParam Map<String,Object> params ){
        LOGGER.info("海外发票查询,param{}",params);
        Query query =new Query(params);
        Integer result = overseasinvoiceService.listCount(query);
        List<OverseasInvoiceEntity> list=overseasinvoiceService.list(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("海外发票导入")
    @RequestMapping("/overseasInvoiceImport")
    public Map overseasInvoiceImport(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("海外发票导入,params {}", multipartFile);
        overseasinvoiceService.delete(getUser().getLoginname());
        Map<String,Object> map = overseasinvoiceService.parseExcel(multipartFile,getUser().getLoginname());
        return map;
    }

}
