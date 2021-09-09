package com.xforceplus.wapp.modules.certification.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
import com.xforceplus.wapp.modules.certification.service.ImportCertificationService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.xforceplus.wapp.modules.certification.WebUriMappingConstant.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 导入认证控制层
 *
 * @author Colin.hu
 * @date 4/19/2018
 */
@RestController
public class ImportCertificationController extends AbstractController {

    private static final Logger LOGGER = getLogger(ImportCertificationController.class);

    private final ImportCertificationService importCertificationService;

    @Autowired
    public ImportCertificationController(ImportCertificationService importCertificationService) {
        this.importCertificationService = importCertificationService;
    }

    @SysLog("导入发票信息")
    @PostMapping(URI_INVOICE_CERTIFICATION_IMPORT)
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入认证，文件开始导入");

        final String schemaLabel = getCurrentUserSchemaLabel();

        return importCertificationService.importEnjoySubsided(schemaLabel,getUserId(),multipartFile);
    }

    @SysLog("导出认证模板")
    @AuthIgnore
    @GetMapping(URI_CERTIFICATION_EXPORT_TEMP)
    public void exportTemplate(HttpServletResponse response) {
        LOGGER.info("导出认证模板");

        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/certification/certificationTemplate.xlsx");
        excelView.write(response, "certificationTemplate");
    }

    @SysLog("提交认证")
    @PostMapping(URI_CERTIFICATION_AUTH_SUBMIT)
    public R authSubmit(@RequestParam Map<String, String> param) {
        LOGGER.info("请求参数为:{}", param);
        final String schemaLabel = getCurrentUserSchemaLabel();
        final Integer count = importCertificationService.submitAuth(schemaLabel,param,getUser().getLoginname(),getUserName());
        return R.ok().put("success", count > 0);
    }
}
