package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.redTicket.entity.FileEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.service.UploadOpenRedTicketDataService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.URI_OPEN_RED_TICKET_LIST_UPLOAD;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by 1 on 2018/11/12 11:45
 */
@RestController
public class UploadOpenRedTicketDataController extends AbstractController {
    private UploadOpenRedTicketDataService uploadOpenRedTicketDataService;
    private static final Logger LOGGER = getLogger(UploadOpenRedTicketDataController.class);
    @Autowired
    public UploadOpenRedTicketDataController(UploadOpenRedTicketDataService uploadOpenRedTicketDataService) {
        this.uploadOpenRedTicketDataService = uploadOpenRedTicketDataService;
    }

    @RequestMapping(URI_OPEN_RED_TICKET_LIST_UPLOAD)
    @SysLog("打印开红票查询列表")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("userCode",getUser().getUsercode());
        LOGGER.info("查询条件为:{}", params);

        //查询列表数据
        Query query = new Query(params);
        Integer result = uploadOpenRedTicketDataService.getRedTicketMatchListCount(query);
        List<RedTicketMatch> list = uploadOpenRedTicketDataService.queryOpenRedTicket(query);

        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);

    }
    @RequestMapping("/modules/openRedTicket/queryOpenRedDataList")
    @SysLog("打印开红票资料文件列表")
    public R redDatalist(@RequestParam Map<String, Object> para) {
        LOGGER.info("查询条件为:{}", para);
      List<FileEntity> list = uploadOpenRedTicketDataService.queryRedDatalist(para);

        return R.ok().put("fileList", list);
    }

    @RequestMapping("/modules/openRedTicket/deleteRedDataFile")
    @SysLog("打印开红票资料文件删除")
    public R deleteRedData(@RequestParam Map<String, Object> para) {
        LOGGER.info("查询条件为:{}", para);
      int i =uploadOpenRedTicketDataService.deleteRedData(para);
        return R.ok();
    }
}
