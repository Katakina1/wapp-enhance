package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
import com.xforceplus.wapp.modules.redTicket.entity.FileEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.service.CheckRedTicketInformationGService;
import com.xforceplus.wapp.modules.redTicket.service.UploadRedNoticeService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.*;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class UploadRedNoticeController extends AbstractController {
    private UploadRedNoticeService examineAndUploadRedNoticeService;
    private static final Logger LOGGER = getLogger(UploadRedNoticeController.class);
    @Autowired
    public UploadRedNoticeController(UploadRedNoticeService examineAndUploadRedNoticeService) {
        this.examineAndUploadRedNoticeService = examineAndUploadRedNoticeService;
    }

    @RequestMapping(URI_OPEN_RED_TICKET_UPLOAD_NOTICES_LIST)
    @SysLog("开红票查询列表")
    public R list(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        Query query = new Query(params);
        Integer result = examineAndUploadRedNoticeService.getRedTicketMatchListCount(query);
        List<RedTicketMatch> list = examineAndUploadRedNoticeService.queryOpenRedTicket(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);

    }
    @RequestMapping("/modules/openRedTicket/queryOpenRedNoticeList")
    @SysLog("打印开红票资料文件列表")
    public R redNoticelist(@RequestParam Map<String, Object> para) {
        LOGGER.info("查询条件为:{}", para);
        List<FileEntity> list = examineAndUploadRedNoticeService.queryRedNoticelist(para);

        return R.ok().put("fileList", list);
    }

    @RequestMapping("/modules/uploadRedNotice/deleteRedNoticeFile")
    @SysLog("上传红字通知单文件删除")
    public R deleteRedData(@RequestParam Map<String, Object> para) {
        LOGGER.info("查询条件为:{}", para);
        int i =examineAndUploadRedNoticeService.deleteRedData(para);
        return R.ok();
    }

    @SysLog("导出红字通知单模板")
    @AuthIgnore
    @GetMapping("export/redTicket/noticeImportExport")
    public void exportTemplate(HttpServletResponse response) {
        LOGGER.info("导出红字通知单模板");

        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/redTicket/noticeImport.xlsx");
        excelView.write(response, "noticeImport");
    }


}
