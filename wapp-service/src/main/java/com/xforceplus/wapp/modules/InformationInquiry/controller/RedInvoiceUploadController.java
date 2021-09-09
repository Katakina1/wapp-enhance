package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.PaymentInvoiceUploadFailExcel;
import com.xforceplus.wapp.modules.InformationInquiry.export.RedInvoiceUploadPrint;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentInvoiceUploadService;
import com.xforceplus.wapp.modules.InformationInquiry.service.RedInvoiceUploadService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
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
 * 红票信息查询
 */
@RestController
public class RedInvoiceUploadController extends AbstractController {

    @Autowired
    private RedInvoiceUploadService redInvoiceUploadService;

    private static final Logger LOGGER = getLogger(RedInvoiceUploadController.class);


    /**
     * 红票信息导入
     */
    @SysLog("红票信息导入")
    @PostMapping(value = "/export/redInvoiceUploadPrint1")
    public Map<String, Object> getInvoiceCheck(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("红票信息导入,params {}", multipartFile);
        Map<String,Object> map = redInvoiceUploadService.parseExcel(multipartFile);
        return map;
    }
    /**
     * 红票信息导入模板下载
     */
    @SysLog("红票信息导入模板下载")
    @RequestMapping(value = "/export/redInvoiceUploadExport")
    public void getInvoiceCheck(HttpServletResponse response) {
        LOGGER.info("红票信息导入模板,params {}");
        //生成excel
        final RedInvoiceUploadPrint excelView = new RedInvoiceUploadPrint();
        excelView.write(response, "redInvoiceUploadExportList");
    }
    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("红票信息列表（销）")
    @RequestMapping("/export/queryList")
    public R querylist(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        Query query = new Query(params);
        query.put("venderid",getUser().getUsercode());
        List<RedInvoiceUploadEntity> list = redInvoiceUploadService.queryList(query);
        ReportStatisticsEntity result = redInvoiceUploadService.queryTotalResult(query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalCount", result.getTotalCount());

    }
    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("红票信息列表")
    @RequestMapping("/export/list")
    public R list(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID
        query.put("userID", getUserId());
        List<RedInvoiceUploadEntity> list = redInvoiceUploadService.queryList(query);
        ReportStatisticsEntity result = redInvoiceUploadService.queryTotalResult(query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalCount", result.getTotalCount());

    }
}
