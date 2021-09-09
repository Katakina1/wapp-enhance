package com.xforceplus.wapp.modules.redInvoiceManager.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.export.InvoiceListExcel;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InvoiceListService;
import com.xforceplus.wapp.modules.redInvoiceManager.service.UploadScarletLetterService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 导出专用controller
 */
@RestController
@RequestMapping("/export")
public class ExportRedInvoiceManagerController extends AbstractController {

    @Autowired
    private InvoiceListService invoiceListService;
    @Autowired
    private UploadScarletLetterService uploadScarletLetterService;
    private static final Logger LOGGER = getLogger(ExportRedInvoiceManagerController.class);
    /**
     * 发票查询导出(销)
     * @param params
     * @return
     */
    @SysLog("发票查询导出")
    @RequestMapping("/redInvoiceManager/invoiceListExport")
    public void paymentInvoiceExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID
        params.put("userID", getUserId());
        UploadScarletLetterEntity list1 = uploadScarletLetterService.getTypeById(schemaLabel,params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        List<UploadScarletLetterEntity> list=null;
        if(list1.getOrgType().equals("7")){
            list = uploadScarletLetterService.queryListByStoreAll(params);
            map.put("invoiceList", list);
        }else if(list1.getOrgType().equals("1")||list1.getOrgType().equals("5")||list1.getOrgType().equals("2")||list1.getOrgType().equals("4")||list1.getOrgType().equals("8")){
            list = invoiceListService.queryListAll(params);
            map.put("invoiceList", list);
        }
       for (UploadScarletLetterEntity ue:list){
        ue.setRedLetterNotice(uploadScarletLetterService.getRedNoticeNumber(ue.getSerialNumber()));
       }
        //生成excel
        final InvoiceListExcel excelView = new InvoiceListExcel(map, "export/redInvoice/InvoiceList.xlsx", "invoiceList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "invoiceList" + excelNameSuffix);

    }

}
