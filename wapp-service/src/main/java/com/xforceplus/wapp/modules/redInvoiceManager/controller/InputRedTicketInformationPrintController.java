package com.xforceplus.wapp.modules.redInvoiceManager.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.export.BatchRedTicketExcel;
import com.xforceplus.wapp.modules.redInvoiceManager.export.InputRedTicketInformationExport;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InputRedTicketInformationService;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.service.EntryRedTicketService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Maps;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
public class InputRedTicketInformationPrintController extends AbstractController {

    @Autowired
    private InputRedTicketInformationService inputRedTicketInformationService;

    private static final Logger LOGGER = getLogger(InputRedTicketInformationPrintController.class);
    private String dirpath ="/home/vn088jh/jxfp/emailFile/";

    @SysLog("导出红票模板")
    @GetMapping("export/redInvoiceManager/InputRedTicketInformation")
    public void exportTemplate(HttpServletResponse response) {
        LOGGER.info("导出红票模板");

        //生成excel
        final InputRedTicketInformationExport excelView = new InputRedTicketInformationExport("export/redInvoice/InputRedTicketInformation.xlsx");
        excelView.write(response, "InputRedTicketInformation");
    }

    @SysLog("导入发票信息")
    @PostMapping("modules/redInvoiceManager/InputRedTicketInformationImport")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入认证，文件开始导入");
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(5);
        Long userId=getUserId();
        List<OrgEntity> gfNameAndTaxNoList = inputRedTicketInformationService.getGfNameAndTaxNo(userId);
        gfNameAndTaxNoList.forEach(org->{
            params.put("xfTaxno",org.getTaxno());
        });


        return inputRedTicketInformationService.importInvoice(params,multipartFile);
    }

    @SysLog("批量导入红票导出excel")
    @RequestMapping("/export/redInvoiceManager/batchRedTicketExport")
    public void batchRedTicketExport(String  params, HttpServletResponse response){
        final String schemaLabel = getCurrentUserSchemaLabel();
        JSONObject o = JSONObject.fromObject(params);
        //查询列表数据
        final Map<String, Object> map = new HashMap<>();
        map.put("store",o.get("store"));
        map.put("redNoticeNumber",o.getString("redNoticeNumber") );
        map.put("createDate1",o.get("createDate1"));
        map.put("createDate2",o.get("createDate2"));
        map.put("userID",getUserId());
        List<UploadScarletLetterEntity> list = inputRedTicketInformationService.queryListAllExport(map);
        map.put("batchRedTicketList",list);
        //生成excel
        final BatchRedTicketExcel excelView = new BatchRedTicketExcel(map, "export/redInvoice/batchRedTicketList.xlsx", "batchRedTicketList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.writeBD(dirpath,"intRedTickeList"+ excelNameSuffix+".xlsx");
        File fi=new File(dirpath+"RedTickeList"+ excelNameSuffix+".xlsx");
        inputRedTicketInformationService.sendEmail(o.get("createDate1").toString(),o.get("createDate2").toString(),fi);
       // excelView.write(response, "batchRedTicketList" + excelNameSuffix);
    }


}
