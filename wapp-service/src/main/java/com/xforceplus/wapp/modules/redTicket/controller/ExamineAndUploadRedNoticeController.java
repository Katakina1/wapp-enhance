package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.export.AuthenticationQueryExcel;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.redTicket.entity.*;
import com.xforceplus.wapp.modules.redTicket.export.RedTicketMatchExamineQueryExcel;
import com.xforceplus.wapp.modules.redTicket.service.CheckRedTicketInformationGService;
import com.xforceplus.wapp.modules.redTicket.service.ExamineAndUploadRedNoticeService;
import com.xforceplus.wapp.modules.redTicket.service.QueryOpenRedTicketDataService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.*;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class ExamineAndUploadRedNoticeController extends AbstractController {
    private ExamineAndUploadRedNoticeService examineAndUploadRedNoticeService;

    private static final Logger LOGGER = getLogger(ExamineAndUploadRedNoticeController.class);
    @Autowired
    public ExamineAndUploadRedNoticeController(ExamineAndUploadRedNoticeService examineAndUploadRedNoticeService) {
        this.examineAndUploadRedNoticeService = examineAndUploadRedNoticeService;
    }
    @Autowired
    QueryOpenRedTicketDataService queryOpenRedTicketDataService;

    @RequestMapping(URI_OPEN_RED_TICKET_EXAMINE_LIST)
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
    @RequestMapping(URI_INVOICE_EXPORT_EXAMINE_DATA)
    @SysLog("导出审核清单")
    public void exportDataExamine(@RequestParam Map<String, Object> param ,HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", param);
        //查询列表数据
        Query query = new Query(param);
        query.remove("limit");
        query.remove("offset");
        //Integer result = examineAndUploadRedNoticeService.getRedTicketMatchListCount(query);

        final Map<String, List<Object>> map = newHashMapWithExpectedSize(1);
        List<RedTicketMatch> list = examineAndUploadRedNoticeService.queryOpenRedTicket(query);
        List<Object> mergeInvoiceDetailList = new ArrayList<>();
        for (RedTicketMatch entity : list) {
            Map<String, Object> map1 = new HashMap();
            map1.put("page",param.get("page"));
            map1.put("limit",param.get("limit"));
            map1.put("redTicketDataSerialNumber",entity.getRedTicketDataSerialNumber());
            map1.put("id",entity.getId());
            Query temp = new Query(map1);
            List<RedTicketMatchDetail> tempList = queryOpenRedTicketDataService.getMergeInvoiceDetailList(temp);
            if(tempList.size()>0){
                RedTicketMatchDetail redTicketMatchDetail = tempList.get(0);
                String businessType= queryOpenRedTicketDataService.selectBusinessType(redTicketMatchDetail.getRedTicketDataSerialNumber());
                redTicketMatchDetail.setBusinessType(businessType);
                List<InvoiceEntity> recordInvoiceList = queryOpenRedTicketDataService.getRecordInvoiceList(temp);
                String uuid = recordInvoiceList.get(0).getInvoiceCode() + recordInvoiceList.get(0).getInvoiceNo();
                InvoiceEntity invoiceEntity = examineAndUploadRedNoticeService.getRedInfo(uuid);
                String name = redTicketMatchDetail.getGoodsName();
                String tax_sortcode = "";
                if(name.indexOf("*") == 0 && name.lastIndexOf("*") != -1 && name.lastIndexOf("*") > name.indexOf("*") +1){
                    tax_sortcode = examineAndUploadRedNoticeService.seletcTaxCode(name.substring(name.indexOf("*")+1,name.lastIndexOf("*")));
                }
                if(null==tax_sortcode){
                    tax_sortcode = "";
                }
                mergeInvoiceDetailList.add(redTicketMatchDetail);
                mergeInvoiceDetailList.add(invoiceEntity);
                mergeInvoiceDetailList.add(tax_sortcode);
            }

        }

       // map.put("redTicketMatchExamineQuery",list);
        map.put("RedTicketInfoList",mergeInvoiceDetailList);
        //生成excel
        final RedTicketMatchExamineQueryExcel excelView = new RedTicketMatchExamineQueryExcel(map, "export/redTicket/RedTicketInfoList.xlsx", "RedTicketInfoList");
        final String excelName = now().toString("yyyyMMdd");
        excelView.write(response, "RedTicketInfoList" + excelName);

    }


    @RequestMapping(URI_OPEN_RED_TICKET_SAVE_EXAMINE_REMARKS)
    @SysLog("保存不同意信息")
    public R saveExamineRemarks(@RequestParam Map<String, Object> para) {
        LOGGER.info("查询条件为:{}", para);
        //保存不同意信息
        String message = examineAndUploadRedNoticeService.saveExamineRemarks(para);

        return R.ok().put("message",message);

    }
    @RequestMapping(URI_INVOICE_OPEN_RED_QUERY_RED_TICKET_AGREE)
    @SysLog("同意信息")
    public R updateMatchStatus(@RequestParam("ids") String  ids) {
        LOGGER.info("查询条件为:{}", ids);
        //保存同意信息
        JSONArray arr = JSONArray.fromObject(ids);
        for (int i = 0; i < arr.size(); i++) {
            long id = Long.valueOf(String.valueOf(arr.get(i))).longValue();
            examineAndUploadRedNoticeService.updateMatchStatus(id);
        }
        return R.ok();

    }
    @RequestMapping(URI_INVOICE_SEND_MESSAGE_DATA)
    @SysLog("同意之后通知税务组")
    public R sendMessageToTax(@RequestParam("ids") String  ids) {
        LOGGER.info("查询条件为:{}", ids);
        Map<String, Object> map= examineAndUploadRedNoticeService.sendMessageToTax(ids);
        if(map.get("success").equals("yes")){
            return R.ok().put("success","ok");
        }else{
            return R.ok().put("success","no");
        }
    }

    @SysLog("红字通知单上传")
    @RequestMapping(value = URI_OPEN_RED_TICKET_RED_NOTIES_UPLOAD, method = {RequestMethod.POST})
    public R uploadRedTicketData(@RequestParam("file") MultipartFile file,@RequestParam("redTicketDataSerialNumber") String redTicketDataSerialNumber,@RequestParam("id") String id, @RequestParam("businessType") String businessType) {
        LOGGER.info("查询条件为:{}", id);
        return R.ok().put("msg", examineAndUploadRedNoticeService.uploadRedTicketRed( file, getUser(), redTicketDataSerialNumber,Integer.valueOf(id),businessType));
    }

    @SysLog("红字通知单批量上传")
    @RequestMapping(value = URI_OPEN_RED_TICKET_RED_NOTIES_UPLOAD_BATH, method = {RequestMethod.POST})
    public R uploadRedTicketRed(@RequestParam("file") MultipartFile file) {
       LOGGER.info("查询条件为:{}", file);
        try {
            Object o = examineAndUploadRedNoticeService.uploadRedTicketRedBatch(file);
            if(o!=null&& !o.toString().isEmpty()&& !o.toString().equals("success")){
               return R.error(488,o.toString());
            }
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("红字通知单批量上传失败:{}", e);
            return R.error(500, e.getMessage());
        }
    }
    @SysLog("撤销审核状态")
    @RequestMapping("modules/openRedTicket/revoke")
    public R revoke(Long id){
        int i = examineAndUploadRedNoticeService.revoke(id);
        if(i>0){
            return R.ok("撤销成功！");
        }
        return R.ok("撤销失败！");
    }
}
