package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.modules.base.service.ReleaseAnnouncementService;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
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

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;


@RestController
public class ExportControllerBase extends AbstractController {

    @Autowired
    private ReleaseAnnouncementService releaseAnnouncementService;

    private static final Logger LOGGER = getLogger(ExportControllerBase.class);

    @SysLog("导出供应商模板")
    @RequestMapping("/export/userImportExport")
    public void userImportExport(HttpServletResponse response) {
        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/base/userImport.xlsx");
        excelView.write(response, "importTemplate");
    }

    @SysLog("导入发票信息")
    @PostMapping("modules/userImport")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入认证，文件开始导入");
        return releaseAnnouncementService.importUser(multipartFile);
    }
}
