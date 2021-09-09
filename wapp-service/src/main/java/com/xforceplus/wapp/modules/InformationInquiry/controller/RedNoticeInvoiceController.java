package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedNoticeBathEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.RedInvoiceNoticeTemplate;
import com.xforceplus.wapp.modules.InformationInquiry.service.RedNoticeInvoiceService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * 索赔查询
 */
@RestController
public class RedNoticeInvoiceController extends AbstractController {
    private static final Logger LOGGER = getLogger(RedNoticeInvoiceController.class);
    @Autowired
    private RedNoticeInvoiceService redNoticeInvoiceService;

    /*
     * 开红票资料导入
     */
    @SysLog("红字通知单Excel导入")
    @PostMapping(value = "/redNotice/redNoticeImport")
    public Map<String, Object> getInvoiceCheck(@RequestParam("file") MultipartFile multipartFile,String redTicketType) {
        LOGGER.info("红字通知单Excel导入,params {}", multipartFile);
            Map<String,Object> map = redNoticeInvoiceService.parseExcel(multipartFile,getUser().getLoginname(), redTicketType);
            return map;

    }
    /*
     * 开红票资料导入
     */
    @SysLog("下载红字通知单模板")
    @GetMapping(value = "/export/redInvoiceNoticeTemplate")
    public void downLoadNoticeTemplate( HttpServletResponse  response) {
        //生成excel
      final RedInvoiceNoticeTemplate excelView = new RedInvoiceNoticeTemplate();
        excelView.write(response, "noticeImport");
    }


    @SysLog("查询红字通知单列表")
    @PostMapping(value = "/modules/redNoticeBatchTicket/selectRedTicketNoticeList")
    public Map<String, Object> getInvoiceCheck(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //当前登录人ID
        query.put("userID", getUserId());
        List<RedNoticeBathEntity> list = redNoticeInvoiceService.queryList(query);
        int result = redNoticeInvoiceService.queryTotalResult(query);

        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil).put("totalCount", result);

    }

    @SysLog("生成红字通知单")
    @PostMapping(value = "/redNotice/createPDF")
    public R createPDF(@RequestBody List<RedNoticeBathEntity> list) {
        return R.ok().put("path", redNoticeInvoiceService.createZip(list));
    }
    @SysLog("下载红字通知单")
    @GetMapping(value = "/export/redNotice/downloadPDF")
    public void downloadPDF(String path, HttpServletResponse  response) {
        redNoticeInvoiceService.downloadPDF(path, response);
    }
}
