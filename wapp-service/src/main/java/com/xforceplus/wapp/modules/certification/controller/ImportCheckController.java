package com.xforceplus.wapp.modules.certification.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.export.CheckTemplateExport;
import com.xforceplus.wapp.modules.certification.service.ImportCheckService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.xforceplus.wapp.modules.certification.WebUriMappingConstant.URI_CERTIFICATION_CHECK_SUBMIT;
import static com.xforceplus.wapp.modules.certification.WebUriMappingConstant.URI_CERTIFICATION_EXPORT_CHECK_TEMP;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 导入勾选控制层
 * @author kevin.wang
 * @date 4/20/2018
 */
@RestController
public class ImportCheckController extends AbstractController {

    private static final Logger LOGGER = getLogger(ImportCheckController.class);

    private final ImportCheckService importCheckService;

    @Autowired
    public ImportCheckController(ImportCheckService importCheckService) {
        this.importCheckService = importCheckService;
    }


    @SysLog("导出勾选模板")
    @AuthIgnore
    @GetMapping(URI_CERTIFICATION_EXPORT_CHECK_TEMP)
    public void exportTemplate(HttpServletResponse response) {
        LOGGER.info("导出勾选模板");

        //生成excel
        final CheckTemplateExport excelView = new CheckTemplateExport();
        excelView.write(response, "checkTemplate");
    }

    @SysLog("导入勾选操作")
    @PostMapping(URI_CERTIFICATION_CHECK_SUBMIT)
    public R submit(@RequestParam Map<String, String> param) {
        LOGGER.info("请求参数为:{}", param);
        final String schemaLabel = getCurrentUserSchemaLabel();
        final Integer count = importCheckService.submit(schemaLabel,param,getLoginName(),getUserName());
        return R.ok().put("success", count > 0);
    }
}
