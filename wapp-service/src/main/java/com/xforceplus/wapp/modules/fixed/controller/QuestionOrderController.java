package com.xforceplus.wapp.modules.fixed.controller;

import static com.xforceplus.wapp.modules.posuopei.constant.Constants.POSUOPEI_PO_INVOICE_DETAIL;
import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.URI_OPEN_RED_TICKET_GET_IMAGE_ALL;
import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.einvoice.controller.EinvoiceQueryController;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.entity.QuestionOrderEntity;
import com.xforceplus.wapp.modules.fixed.service.QuestionOrderService;
import com.xforceplus.wapp.modules.posuopei.entity.DetailEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.posuopei.entity.InvoicesEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;

@RestController
@RequestMapping("/modules/fixed/QuestionOrderCheck")
public class QuestionOrderController extends AbstractController{
    private static final Logger LOGGER = getLogger(QuestionOrderController.class);
    
    @Autowired
    private QuestionOrderService questionOrderService;
    
    /*
     * 问题单查询
     */
    @SysLog("问题单查询")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
    	final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        List<QuestionOrderEntity> Questionlist = questionOrderService.queryCheckOrderList(schemaLabel,query);
        //ReportStatisticsEntity result = compreh ensiveInvoiceQueryService.queryTotalResult(schemaLabel,query);
        Integer countOrder = questionOrderService.countOrders( schemaLabel,query);
        PageUtils pageUtil = new PageUtils(Questionlist, countOrder, query.getLimit(), query.getPage());

        return R.ok().put("page",pageUtil);
        
      //  PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

      //  return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }
    	
    
    @SysLog("发票信息查询")
    @RequestMapping("/invoiceInfo")
    public R invoiceInfo(@RequestParam Map<String, Object> params){
    	final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
      //  Query query = new Query(params);
        List<InvoiceEntity> Questionlist = questionOrderService.queryInvoice(schemaLabel,params);
        //ReportStatisticsEntity result = compreh ensiveInvoiceQueryService.queryTotalResult(schemaLabel,query);
        //Integer countOrder = questionOrderService.countOrders( schemaLabel,query);
        PageUtils pageUtil = new PageUtils(Questionlist, Questionlist.size(), 0, 0);
        
        
        return R.ok().put("page",Questionlist);
    	
    }
    @SysLog("订单信息查询")
    @RequestMapping("/orderInfo")
    public R orderInfo(@RequestParam Map<String, Object> params){
    	final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        List<OrderEntity> orderlist = questionOrderService.queryOrder(schemaLabel,params);
       
        return  R.ok().put("order",orderlist);
    	
    }
    
    
    @SysLog("文件信息查询")
    @RequestMapping("/fileInfo")
    public R fileInfo(@RequestParam Map<String, Object> params){
    	final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
      //  Query query = new Query(params);
       // List<InvoiceEntity> Questionlist = questionOrderService.queryInvoice(schemaLabel,params);
        //ReportStatisticsEntity result = compreh ensiveInvoiceQueryService.queryTotalResult(schemaLabel,query);
        //Integer countOrder = questionOrderService.countOrders( schemaLabel,query);
       // PageUtils pageUtil = new PageUtils(Questionlist, Questionlist.size(), 0, 0);
        List<FileEntity> filelist = questionOrderService.queryFileName(schemaLabel,params);
       
        for (FileEntity fileEntity : filelist) {
        	String filepath=fileEntity.getFilePath();
        	if((filepath.toLowerCase()).endsWith(".bmp")||
        			(filepath.toLowerCase()).endsWith(".psd")||
        			(filepath.toLowerCase()).endsWith(".tiff")||
        			(filepath.toLowerCase()).endsWith(".jpg")||
        			(filepath.toLowerCase()).endsWith(".png")||
        			(filepath.toLowerCase()).endsWith(".gif")||
        			(filepath.toLowerCase()).endsWith(".jpeg")){
        		
        		fileEntity.setFileType("0");
        	}
		}
        return  R.ok().put("file",filelist);
    	
    }
    
    @SysLog("获取图片--资料")
    @RequestMapping(value="/getImageForAll", method = {RequestMethod.GET})
    public void getImageForAll(@RequestParam("id") Long id, HttpServletResponse response) {
        LOGGER.debug("----------------获取图片资料--------------------");
        //final String schemaLabel = getCurrentUserSchemaLabel();
        questionOrderService.getInvoiceImageForAll( id, getUser(), response);
    }

    @SysLog("下载文件")
    @RequestMapping("/downloadFile")
    public void downloadFile(@RequestParam("id")Long id, HttpServletResponse response) {
        //根据id查询文件信息(文件路径和文件名)
       FileEntity fileEntity = questionOrderService.getFileInfo(id);
    	   try{
    		   
    		   String[] type=fileEntity.getFilePath().split("\\.");
    		   //查看图片
    		   questionOrderService.downloadFile(fileEntity.getFilePath(), fileEntity.getFileName(), response);
    	   }catch(Exception e){
    		   LOGGER.debug("----------------文件路径没有,不能下载--------------------");
    		   e.printStackTrace();
    	   }

    }

    
    /**
     * 点击明细按钮后的查询
     * @param value
     * @return
     */
    @SysLog("明细")
    @RequestMapping("/invoice")
    public R getInvoinceDetail(@RequestBody String value){
        final String schemaLabel = getCurrentUserSchemaLabel();
        R.error("未找到明细信息");
        InvoicesEntity invoiceEntity;
        if(StringUtils.isEmpty(value) || "null".equals(value)) {
            return R.error(1,"未查找到明细信息");
        }
        Long id=Long.parseLong(value);
        try{
            invoiceEntity=questionOrderService.getDetailInfo(schemaLabel,id);
        }catch(Exception e){
            LOGGER.error("明细 {}",e);
            return R.error(1,"未查找到明细信息");
        }

        final List<InvoicesEntity> outList = questionOrderService.getOutInfo(schemaLabel, invoiceEntity.getInvoiceCode()+invoiceEntity.getInvoiceNo());

        R r= R.ok();
        String flag=invoiceEntity.getInvoiceType();
        final String vehicleInvoice="03";//机动车销售统一发票
        if (flag.equals(vehicleInvoice)){
            DetailVehicleEntity detailVehicleEntity=new DetailVehicleEntity();
            try {
                detailVehicleEntity=questionOrderService.getVehicleDetail(schemaLabel, id);
            }catch (Exception e){
                LOGGER.error(e.toString());
            }


            String account="";
            String phone="";
            if (checkBankAccount(invoiceEntity.getXfBankAndNo())!=null){
                account=checkBankAccount(invoiceEntity.getXfBankAndNo());
            }
            if (checkCellphone(invoiceEntity.getXfAddressAndPhone())!=null){
                phone=checkCellphone(invoiceEntity.getXfAddressAndPhone());
            }else{
                phone=checkTelephone(invoiceEntity.getXfAddressAndPhone());
            }

            String bank = invoiceEntity.getXfBankAndNo().replace(account,"");
            String address=invoiceEntity.getXfAddressAndPhone().replace(phone,"");
            r.put("bank",bank).put("account", account ).put("address", address).put("phone", phone);
            r.put("invoiceEntity",invoiceEntity);
            r.put("outList", outList);
            r.put("detailVehicleEntity",detailVehicleEntity);
            return r;
        }
        List<DetailEntity> detailEntityList=questionOrderService.getInvoiceDetail(schemaLabel, id);
        BigDecimal detailAmountTotal=new BigDecimal("0.0");
        BigDecimal taxAmountTotal=new BigDecimal("0.0");
        for (DetailEntity detailEntity:detailEntityList){
            detailAmountTotal=detailAmountTotal.add(new BigDecimal(detailEntity.getDetailAmount()));
            taxAmountTotal=taxAmountTotal.add(new BigDecimal(detailEntity.getTaxAmount()));
        }


        r.put("detailEntityList",detailEntityList);
        r.put("invoiceEntity", invoiceEntity);
        r.put("outList", outList);
        r.put("detailAmountTotal",detailAmountTotal);
        r.put("taxAmountTotal",taxAmountTotal);
        return  r;
    }
    
    
    @SysLog("问题单审批")
    @RequestMapping("/check")
    public R check( @RequestBody Map<String,Object> param) {
    	List idList=(List) param.get("ids");
    	for (Object object : idList) {
    		param.put("id", object);
    	 final Boolean flag=questionOrderService.check(param);
    		if(flag){
    			//return R.ok().put("msg","保存审核结果成功！");
    		}else {
    			return R.ok().put("msg","1");
    			
    		}
		}
		return R.ok().put("msg","0");


    }
    
    /**
     * 查询符合的固定电话
     * @param str
     */
    public static String checkTelephone(String str){
        if(str==null){
            return "";
        }
        // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
            //查找到符合的即输出
            return matcher.group();
        }
        return null;
    }
    /**
     * 查询符合的银行帐号
     * @param str
     */
    public static String checkBankAccount(String str){
        if(str==null){
            return "";
        }
        // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("\\d{16,19}");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
            //查找到符合的即输出
            return matcher.group();
        }
        return null;
    }
    /**
     * 查询符合的手机号码
     * @param str
     */
    public static String checkCellphone(String str){
        if(str==null){
            return "";
        }
        // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("((1[0-9]))\\d{9}");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
            //查找到符合的即输出
            return matcher.group();
        }
        return null;
    }
    
    
 
}
