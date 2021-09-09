package com.xforceplus.wapp.modules.signin.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.service.ImportSignService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 导入签收
 * @author Colin.hu
 * @date 4/23/2018
 */
@RestController
@RequestMapping("modules/signIn/")
public class ImportSignController extends AbstractController {

    private final static Logger LOGGER = getLogger(ImportSignController.class);

    private final ImportSignService importSignService;

    @Autowired
    public ImportSignController(ImportSignService importSignService) {
        this.importSignService = importSignService;
    }

    @SysLog("excel导入签收")
    @PostMapping("excelSign")
    public R importSignExcel(@RequestParam("file") MultipartFile multipartFile, @RequestParam("count") Integer count) {
        LOGGER.info("excel导入签收");

        try {
            final List<RecordInvoiceEntity> recordInvoiceEntityList = importSignService.importSignExcel(buildExportEntity(), multipartFile, count);
            return R.ok().put("page",recordInvoiceEntityList);
        } catch (ExcelException | RRException e) {
            LOGGER.error("导入签收失败，excel失败:{}", e);
            return R.error(9999, e.getMessage());
        }
    }

    @SysLog("图片导入签收")
    @PostMapping("imgSign")
    public R importSignImg(@RequestParam("file") MultipartFile multipartFile, @RequestParam("count") Integer count) {
        LOGGER.info("图片导入签收");

        //图片处理
        final List<RecordInvoiceEntity> recordInvoiceEntityList = importSignService.importSignImg(buildExportEntity(), multipartFile, count);

        if (!recordInvoiceEntityList.isEmpty()) {
            return R.ok().put("page", recordInvoiceEntityList);
        }
        //失败返回
        return R.error(9999, "图片处理失败，请联系管理员");
    }

    @SysLog("获取图片")
    @RequestMapping(value = "getSignInImg", method = {RequestMethod.POST})
    public R getInvoiceImage(@RequestParam Map<String, String> params) {
       LOGGER.info("获取图片的请求参数为:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        return R.ok().put("img",importSignService.getInvoiceImage(params));
    }

    @SysLog("保存修改")
    @RequestMapping(value = "modify", method = {RequestMethod.POST})
    public R modifyInvoice(@RequestParam Map<String, String> params) {
        LOGGER.info("获取图片的请求参数为:{}", params);

        return R.ok().put("result",importSignService.modifyInvoice(params, buildExportEntity()));
    }

    /**
     * 构建实体
     * @return 实体
     */
    private ExportEntity buildExportEntity() {
        final ExportEntity exportEntity = new ExportEntity();
        exportEntity.setSchemaLabel(getCurrentUserSchemaLabel());
        //人员id
        exportEntity.setUserId(getUserId());
        //帐号
        exportEntity.setUserAccount(getUser().getLoginname());
        //人名
        exportEntity.setUserName(getUserName());
        return exportEntity;
    }
}
