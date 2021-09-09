package com.xforceplus.wapp.modules.einvoice.controller;


import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.service.EinvoiceUploadService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_DELETE;
import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_FOR_IMAGE_CHECK;
import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_GET_IMAGE;
import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_GET_IMAGE_ALL;
import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_SAVE;
import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_UPDATE_SAVE;
import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_UPDATE_SELECT;
import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_UPLOAD;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 电票上传控制层
 *
 * @author Marvin.zhong
 */

@RestController
public class ElectronInvoiceUploadController extends AbstractController {
    private static final Logger LOGGER = getLogger(ElectronInvoiceUploadController.class);

    @Autowired
    private EinvoiceUploadService einvoiceUploadService;

    @SysLog("电票上传")
    @RequestMapping(value = ELECTRON_INVOICE_UPLOAD, method = {RequestMethod.POST})
    public R uploadElectronInvoice(@RequestParam("file") MultipartFile file) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        return R.ok().put("list", einvoiceUploadService.uploadElectronInvoice(schemaLabel, file, getUser()));
    }

    @SysLog("保存录入的电票")
    @RequestMapping(value = ELECTRON_INVOICE_SAVE, method = {RequestMethod.POST})
    public R saveElectronInvoice(@RequestBody ElectronInvoiceEntity invoiceEntity) {
        LOGGER.debug("----------------电票手工录入保存--------------------");
        final String schemaLabel = getCurrentUserSchemaLabel();
        return R.ok().put("invoice", einvoiceUploadService.saveInputElectronInvoice(schemaLabel, invoiceEntity, getUser()));
    }

    @SysLog("删除电票")
    @RequestMapping(value = ELECTRON_INVOICE_DELETE, method = {RequestMethod.POST})
    public R uploadElectronInvoice(@RequestParam("id") Long id) {
        LOGGER.debug("----------------电票删除--------------------");
        final String schemaLabel = getCurrentUserSchemaLabel();
        Boolean flag = einvoiceUploadService.deleteElectronInvoice(schemaLabel, id);
        String msg = flag ? "删除成功！" : "删除失败！";

        if (flag) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("code", 1);
            map.put("msg", msg);
            return R.ok(map);
        }
        return R.error(msg);
    }

    @SysLog("修改电票查询")
    @RequestMapping(value = ELECTRON_INVOICE_UPDATE_SELECT, method = {RequestMethod.POST})
    public R getInvoiceToUpdate(@RequestParam("id") Long id) {
        LOGGER.debug("----------------电票修改查询--------------------");
        final String schemaLabel = getCurrentUserSchemaLabel();
        return R.ok().put("invoice", einvoiceUploadService.selectElectronInvoice(schemaLabel, id, null));
    }

    @SysLog("修改电票保存")
    @RequestMapping(value = ELECTRON_INVOICE_UPDATE_SAVE, method = {RequestMethod.POST})
    public R updateInvoiceToSave(@RequestBody ElectronInvoiceEntity invoiceEntity) {
        LOGGER.debug("----------------电票修改保存--------------------");
        final String schemaLabel = getCurrentUserSchemaLabel();
        return R.ok().put("invoice", einvoiceUploadService.saveUpdateElectronInvoice(schemaLabel, invoiceEntity, getUserId()));
    }

    @SysLog("获取图片")
    @RequestMapping(value = ELECTRON_INVOICE_GET_IMAGE, method = {RequestMethod.GET})
    public R getInvoiceImage(@RequestParam("id") Long id, HttpServletResponse response) {
        LOGGER.debug("----------------获取图片--------------------");
        final String schemaLabel = getCurrentUserSchemaLabel();
        return R.ok().put("img", einvoiceUploadService.getInvoiceImage(schemaLabel, id, getUser()));
    }

    @SysLog("获取图片--all")
    @RequestMapping(value = ELECTRON_INVOICE_GET_IMAGE_ALL, method = {RequestMethod.GET})
    public void getInvoiceImageForAll(@RequestParam("id") Long id, HttpServletResponse response) {
        LOGGER.debug("----------------获取图片for All--------------------");
        final String schemaLabel = getCurrentUserSchemaLabel();
        einvoiceUploadService.getInvoiceImageForAll(schemaLabel, id, getUser(), response);
    }

    @SysLog("TOKEN CHECK EXPIRE")
    @RequestMapping(value = ELECTRON_INVOICE_FOR_IMAGE_CHECK, method = {RequestMethod.POST})
    public R checkTokenForGetImage() {
        return R.ok();
    }
}
