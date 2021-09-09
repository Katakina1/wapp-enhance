package com.xforceplus.wapp.modules.signin.controller;

import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

//import org.json.JSONObject;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.check.InvoiceCheckConstants;
import com.xforceplus.wapp.modules.check.service.InvoiceCheckModulesService;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.service.ImportSignService;
import com.xforceplus.wapp.modules.signin.service.SignatureProcessingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 发票底账信息查询
 * @author jingsong.mao
 * @date 7/30/2018
 */
@Api(tags="客户端-发票底账信息查询")
@RestController
@RequestMapping("/onlineQuery")
public class ImportSignHttpQueryController extends AbstractController {

    private final static Logger LOGGER = getLogger(ImportSignHttpQueryController.class);

    private final ImportSignService importSignService;
    
    private SignatureProcessingService signatureProcessingService;
    
    private InvoiceCheckModulesService invoiceCheckModulesService;

    @Autowired
    public ImportSignHttpQueryController(ImportSignService importSignService,SignatureProcessingService signatureProcessingService,InvoiceCheckModulesService invoiceCheckModulesService) {
        this.importSignService = importSignService;
        this.signatureProcessingService = signatureProcessingService;
        this.invoiceCheckModulesService = invoiceCheckModulesService;
    } 
   
    @ApiOperation("发票底账信息查询")
    @SysLog("发票底账信息查询")
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String invoiceQueryHttp(String userId, String token,String ocrMapJson) {
    	
    	boolean successFlag = false;
    	
    	JSONObject json = new JSONObject();
           
       	JSONArray array = new JSONArray();
       	JSONObject funcj = new JSONObject();
       	
       	DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");  
    	
    	
    	String jsonString =  ocrMapJson;

        JSONObject jasonObject = JSONObject.fromObject(jsonString);
        
        Map<String, Object> ocrMap = (Map)jasonObject;
        
        if (InvoiceCheckConstants.INVOICE_TYPE.contains(CommonUtil.getFplx(valueOf(ocrMap.get("invoiceCode"))))) {
        	// 普票
        	if(ocrMap.get("checkCode") == null){
        		json.put("success", false);
             	json.put("message", "普票必须有6位或20位校验码");
     			json.put("userId", userId);
     			
     			return json.toString();
        	}
        	
        	String checkCode= ocrMap.get("checkCode").toString();
            if(checkCode.length() != 6 && checkCode.length() != 20){
            	json.put("success", false);
             	json.put("message", "普票必须有6位或20位校验码");
     			json.put("userId", userId);
     			
     			return json.toString();
            }
            
            if(checkCode.length() == 20){
            	ocrMap.put("checkCode", checkCode.substring(checkCode.length() - 6));
            }
        } else {
        	// 专票
        	if(ocrMap.get("invoiceAmount") == null){
        		json.put("success", false);
             	json.put("message", "专票必须有发票金额");
     			json.put("userId", userId);
     			
     			return json.toString();
        	} 
        	
        	String invoiceAmount = ocrMap.get("invoiceAmount").toString();
        	
        	if(invoiceAmount.isEmpty()){
        		json.put("success", false);
             	json.put("message", "专票必须有发票金额");
     			json.put("userId", userId);
     			
     			return json.toString();
        	}
        }
        
        String errorMessage = null;
        
        LOGGER.info("发票底账信息查询");

        // String token = request.getParameter("token");
         
         InvoiceCollectionInfo info = importSignService.queryInvoiceInfo(getCurrentUserSchemaLabel(), ocrMap.get("invoiceNo").toString(), ocrMap.get("invoiceCode").toString());

         
         
 		try {
 			if(info!=null && info.getId()!=null ){
 				funcj.put("id", info.getId());
 				funcj.put("invoiceCode", info.getInvoiceCode());
 				funcj.put("invoiceNo", info.getInvoiceNo());
 				funcj.put("invoiceDate", format1.format(info.getInvoiceDate()));
 				funcj.put("checkCode", info.getCheckCode());
 				funcj.put("gfTaxNo", info.getGfTaxNo());
 				funcj.put("gfName", info.getGfName());
 				funcj.put("taxAmount", info.getTaxAmount());
 				funcj.put("invoiceAmount", info.getInvoiceAmount());
 				funcj.put("totalAmount", info.getTotalAmount());
 				funcj.put("totalAmount", info.getTotalAmount());
 				funcj.put("xfName", info.getXfName());
 				funcj.put("xfTaxNo", info.getXfTaxNo());
 				funcj.put("invoiceType", info.getInvoiceType());
 				
 				successFlag = true;
 			}else{

 				 ocrMap.put("invoiceDate", ocrMap.get("invoiceDate").toString().replace("-", ""));
 				 
 				 ocrMap.put("invoiceType", ocrMap.get("fplx"));
 				  //获取分库分表的入口
		         final String schemaLabel = getCurrentUserSchemaLabel();
		         UserEntity user = getUser();
		         ocrMap.put("user", user);     
		         
		       //  Map<String, Object> returnMap =  invoiceCheckModulesService.doInvoiceCheck(schemaLabel, ocrMap, user.getLoginname());
		         
		       //专票--普票的发票查验---begin
		       try {
		    	  
		    	   ResponseInvoice infor =	signatureProcessingService.checkPlainInvoice(getCurrentUserSchemaLabel(), ocrMap);
		   			
		    	 //  ResponseInvoice infor = (ResponseInvoice)returnMap.get("responseInvoice");//signatureProcessingService.checkPlainInvoice(schemaLabel, ocrMap);
				      
				      if(infor!=null && infor.getBuyerName()!=null ){
				    	  
			 		   				funcj.put("invoiceCode", infor.getInvoiceCode());
			 		   				funcj.put("invoiceNo", infor.getInvoiceNo());
			 		   				funcj.put("invoiceDate", infor.getInvoiceDate());
			 		   				funcj.put("checkCode", infor.getCheckCode());
			 		   				funcj.put("gfTaxNo", infor.getBuyerTaxNo());
			 		   				funcj.put("gfName", infor.getBuyerName());
			 		   				funcj.put("taxAmount", infor.getTaxAmount());
			 		   				funcj.put("invoiceAmount", infor.getInvoiceAmount());
			 		   				funcj.put("totalAmount", infor.getTotalAmount());
			 		   				funcj.put("xfName", infor.getSalerName());
			 		   				funcj.put("xfTaxNo", infor.getSalerTaxNo());
			 		   				funcj.put("invoiceType", infor.getInvoiceType());
			 		   				
			 		   			   successFlag = true;
			 		   		}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       
 			}
 			array.add(funcj);
 			json.put("resultSet", array);
 		} catch (JSONException e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		}
 		
         try {
         	json.put("success", successFlag);
         	json.put("message", successFlag?"电子底账信息查询成功":"电子底账信息查询失败");
 			json.put("userId", userId);
 		} catch (JSONException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    	
       
        return json.toString();
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
