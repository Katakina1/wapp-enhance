package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;

import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.service.EntryRedTicketService;

import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.common.utils.ShiroUtils.getUserId;
import static com.xforceplus.wapp.modules.posuopei.constant.Constants.POSUOPEI_INVOICE_QUERY;
import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by 1 on 2018/10/26 17:13
 */
@RestController
public class EntryRedTicketController  extends AbstractController {
    private EntryRedTicketService entryRedTicketService;
    private static final Logger LOGGER = getLogger(EntryRedTicketController.class);


    @Autowired
    public EntryRedTicketController(EntryRedTicketService entryRedTicketService) {
        this.entryRedTicketService = entryRedTicketService;
    }

    @RequestMapping(URI_RDE_TICKET_LIST)
    @SysLog("红票查询列表")
    public R list(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        params.put("userCode",getUser().getUsercode());
        Query query = new Query(params);
        Integer result = entryRedTicketService.selectRedTicketListCount(query);
        List<RedTicketMatch> list = entryRedTicketService.selectRedTicketList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);

    }


    /**
     * 发票带出
     * @param params
     * @return
     */
    @SysLog("发票带出")
    @PostMapping(value = URI_RDE_RED_TICKET_INVOICE)
    public Map<String, Object> invoiceQuery(@RequestBody Map<String,Object> params){
        LOGGER.info("发票带出,param{}",params);
        Long userId=getUserId();
        List<OrgEntity> gfNameAndTaxNoList = entryRedTicketService.getGfNameAndTaxNo(userId);
        Map<String,Object> map =new HashMap<>();
        /*gfNameAndTaxNoList.forEach(org->{
            params.put("gfTaxno",org.getTaxno());
        });*/
        //获取销方税号
        String xfTaxNo = entryRedTicketService.getXfTaxno(getUser().getOrgid());
        params.put("xfTaxNo",xfTaxNo);
        if(!CommonUtil.isValidNum((String)params.get("invoiceCode"),"^(\\d{10}|\\d{12})$")) {
            map.put("msg","发票代码格式错误");
            return map;
        }else if(!CommonUtil.isValidNum((String)params.get("invoiceNo"),"^[\\d]{8}$")){
            map.put("msg","发票号码格式错误");
            return map;
        }else if(!"01".equals(CommonUtil.getFplx((String)params.get("invoiceCode")))){
            map.put("msg","不是专票代码");
            return map;
        }
        PagedQueryResult<InvoiceEntity> poEntityPagedQueryResult=entryRedTicketService.invoiceQueryOut(params);
        if(!(poEntityPagedQueryResult.getMsg()==null)){
            map.put("msg",poEntityPagedQueryResult.getMsg());
            return map;
        }

        return map;
    }


    /**
     * 发票录入
     * @param params
     * @return
     */
    @SysLog("发票录入")
    @PostMapping(value = URI_RDE_RED_SAVE_TICKET_INVOICE)
    public Map<String, Object> invoiceIn(@RequestParam Map<String,Object> params){
        LOGGER.info("发票录入,param{}",params);
        Boolean flag=true;
        Long userId=getUserId();
        Map<String,Object> map =new HashMap<>();
       // List<OrgEntity> gfNameAndTaxNoList = entryRedTicketService.getGfNameAndTaxNo(userId);
        //获取红字通知单 和红冲总金额
      //  RedTicketMatch redTicketMatch = entryRedTicketService.selectNoticeById(params);

        /*gfNameAndTaxNoList.forEach(org->{
            *//*if(params.get("gfName").equals(org.getOrgname())){*//*
                params.put("gfTaxno",org.getTaxno());
           *//* }*//*
        });*/
        String xfTaxNo = entryRedTicketService.getXfTaxno(getUser().getOrgid());
        params.put("xfTaxNo",xfTaxNo);
        //params.put("redTotalAmount",redTicketMatch.getRedTotalAmount());
        if(!CommonUtil.isValidNum((String)params.get("invoiceCode"),"^(\\d{10}|\\d{12})$")) {
            map.put("msg","发票代码格式错误");
            return map;
        }else if(!CommonUtil.isValidNum((String)params.get("invoiceNo"),"^[\\d]{8}$")){
            map.put("msg","发票号码格式错误");
            return map;
        }else if(!"01".equals(CommonUtil.getFplx((String)params.get("invoiceCode")))){
            map.put("msg","不是专票代码");
            return map;
        }
        BigDecimal invoiceAmount=new BigDecimal((String)params.get("invoiceAmount"));
        BigDecimal taxRate=new BigDecimal((String) params.get("taxRate")).divide(new BigDecimal(100));
        BigDecimal taxRate1=new BigDecimal((String) params.get("taxRate1")).divide(new BigDecimal(100));
        BigDecimal taxAmount=new BigDecimal((String)params.get("taxAmount"));
        BigDecimal totalAmount=new BigDecimal((String)params.get("totalAmount"));
        BigDecimal redTotalAmount=new BigDecimal((String)params.get("redTotalAmount"));

        if(invoiceAmount.compareTo(new BigDecimal(0))>0){
            map.put("msg","红票金额要小于0");
            return map;
        }
        if(taxRate.compareTo(taxRate1)!=0){
            map.put("msg","税率有误");
            return map;
        }
        if(taxAmount.compareTo(new BigDecimal(0))>0){
            map.put("msg","红票税额要小于0");
            return map;
        }
        if(totalAmount.compareTo(new BigDecimal(0))>0){
            map.put("msg","红票价税合计要小于0");
            return map;
        }
        if(((String)params.get("businessType")).equals("1") ||((String)params.get("businessType")).equals("3" )){
            if(invoiceAmount.abs().compareTo(redTotalAmount)!=0){
                map.put("msg","价税合计输入错误");
                return map;
            }
            if( taxAmount.compareTo((invoiceAmount.multiply(taxRate)).setScale(2,BigDecimal.ROUND_HALF_UP))!=0){
                map.put("msg","金额、税率、税额 输入有误");
                return map;
            }
            if( totalAmount.compareTo((invoiceAmount.add(taxAmount)).setScale(2,BigDecimal.ROUND_HALF_UP))!=0){
                map.put("msg","金额、价税合计、税额 输入有误");
                return map;
            }
        }
        if(((String)params.get("businessType")).equals("2")){
            if(totalAmount.abs().compareTo((invoiceAmount.multiply(taxRate.add(new BigDecimal(1)))).setScale(2,BigDecimal.ROUND_HALF_UP).abs())!=0 ){
                map.put("msg","价税合计输入错误");
                return map;
            }
            if(redTotalAmount.abs().compareTo((invoiceAmount.multiply(taxRate.add(new BigDecimal(1)))).setScale(2,BigDecimal.ROUND_HALF_UP).abs())!=0 ){
                map.put("msg","价税合计输入错误");
                return map;
            }
            if( taxAmount.compareTo((invoiceAmount.multiply(taxRate)).setScale(2,BigDecimal.ROUND_HALF_UP))!=0){
                map.put("msg","金额、税率、税额 输入有误");
                return map;
            }
            if( totalAmount.compareTo((invoiceAmount.add(taxAmount)).setScale(2,BigDecimal.ROUND_HALF_UP))!=0){
                map.put("msg","金额、价税合计、税额 输入有误");
                return map;
            }
            if( totalAmount.abs().compareTo(redTotalAmount)!=0){
                map.put("msg","金额、价税合计、税额 输入有误");
                return map;
            }

        }

        PagedQueryResult<InvoiceEntity> poEntityPagedQueryResult=entryRedTicketService.invoiceQueryList(params);
        if(poEntityPagedQueryResult.getMsg()!=null){
            map.put("msg",poEntityPagedQueryResult.getMsg());
            return map;
        }
        return map;
    }


    @RequestMapping(URI_RDE_RED_SELECT_TICKET_INVOICE_BY_ID)
    @SysLog("红票查询列表")
    public R selectRedTicketById(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        params.put("userId",getUserId());
        RedTicketMatch invoiceEntity = entryRedTicketService.selectRedTicketById(params);
        return R.ok().put("invoiceEntity", invoiceEntity);

    }


    @SysLog("导出红票模板")
    @AuthIgnore
    @GetMapping("export/redTicket/invoiceImportExport")
    public void exportTemplate(HttpServletResponse response) {
        LOGGER.info("导出红票模板");

        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/redTicket/invoiceImport.xlsx");
        excelView.write(response, "importTemplate");
    }

    @SysLog("导入发票信息")
    @PostMapping("modules/redTicket/invoiceImport")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入红票，文件开始导入");
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(5);
        String xfTaxNo = entryRedTicketService.getXfTaxno(getUser().getOrgid());
        params.put("xfTaxNo",xfTaxNo);
        return entryRedTicketService.importInvoice(params,multipartFile);
    }

}
