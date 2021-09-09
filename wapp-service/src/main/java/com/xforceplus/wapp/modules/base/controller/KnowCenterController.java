package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.KnowledgeFileEntity;
import com.xforceplus.wapp.modules.base.export.KnowCenterExcel;
import com.xforceplus.wapp.modules.base.service.KnowCenterService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
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

@RestController
public class KnowCenterController extends AbstractController {
    private final static Logger LOGGER = getLogger(KnowCenterController.class);

    @Autowired
    private KnowCenterService knowCenterService;

    @SysLog("知识中心列表查询")
    @RequestMapping("knowCenter/list")
    public R list(@RequestParam Map<String, Object> params) {

        //查询列表数据
        Query query = new Query(params);
        List<KnowledgeFileEntity> list = knowCenterService.queryList(query);
        Integer count = knowCenterService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @PostMapping("knowCenter/upload")
    public R uploadKnow(@RequestParam("file") MultipartFile multipartFile,@RequestParam("venderType") String venderType) {
        LOGGER.info("知识中心文件上传：" +multipartFile);
        try {
            knowCenterService.uploadKnow(multipartFile,venderType);
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("知识中心文件上传失败:{}", e);
            return R.error(500, e.getMessage());
        }
    }

    @SysLog("知识中心下载文件")
    @RequestMapping(value = "knowCenter/downLoadFile")
    public void downLoadFile(@RequestParam("path") String path,@RequestParam("fileName") String fileName, HttpServletResponse response) {
        LOGGER.debug("---------------知识中心下载文件--------------------");
        knowCenterService.getDownLoadFile(path,fileName, response);
    }

    @SysLog("知识中心删除文件")
    @RequestMapping(value = "knowCenter/deleteFile")
    public R deleteOne(@RequestBody List<KnowledgeFileEntity> list) {
        LOGGER.debug("---------------知识中心删除文件--------------------");
        try {
            for (KnowledgeFileEntity entity : list) {
                knowCenterService.deleteOne(entity);
            }
        } catch (JSchException e) {
            e.printStackTrace();
            return R.ok().put("msg","删除失败！");
        } catch (SftpException e) {
            e.printStackTrace();
            return R.ok().put("msg","删除失败！");
        }
        return R.ok().put("msg","删除成功！");
    }

    @SysLog("知识中心导出")
    @AuthIgnore
    @RequestMapping("export/knowCenter")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final Map<String, List<KnowledgeFileEntity>> map = newHashMapWithExpectedSize(1);
        map.put("knowledgeFileList", knowCenterService.queryList(params));
        //生成excel
        final KnowCenterExcel excelView = new KnowCenterExcel(map, "export/knowCenter/knowledgeFileList.xlsx", "knowledgeFileList");
        final String excelName = String.valueOf(new Date().getTime());
        excelView.write(response, "knowledgeFileList" + excelName);
    }
}
