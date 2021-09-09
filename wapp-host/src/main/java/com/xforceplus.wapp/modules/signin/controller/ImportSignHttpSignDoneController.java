package com.xforceplus.wapp.modules.signin.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.modules.signin.entity.InvoiceSavePo;
import com.xforceplus.wapp.modules.signin.entity.SignedInvoiceVo;
import com.xforceplus.wapp.modules.signin.service.ScannerSignService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 图片签收
 * @author jingsong.mao
 * @date 7/30/2018
 */
@Api(tags="客户端-图片签收保存")
@RestController
@RequestMapping("/onlineSignDone")
public class ImportSignHttpSignDoneController extends AbstractController {

    private final static Logger LOGGER = getLogger(ImportSignHttpSignDoneController.class);

    @Autowired
    private ScannerSignService scannerSignService;

    
    @ApiOperation("图片签收保存")
    @SysLog("图片签收保存")
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String imgSignDoneHttp(String userId, String token,String ocrMapJson,String scanId) {
       
    	LOGGER.info("图片签收-图片签收保存");
    	 //获取request里的参数
        
        LOGGER.info("scanId:"+scanId);
        
        LOGGER.info("传过来的ocrMapJson"+ocrMapJson);
        
//		try {
//			ocrMapJson = new String(ocrMapJson.getBytes("ISO-8859-1"), "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		  LOGGER.info("转码后的ocrMapJson"+ocrMapJson);

        JSONObject jasonObject = JSONObject.fromObject(ocrMapJson);
        
        Map<String, String> ocrMap = (Map)jasonObject;
        
       
        
       // List<RecordInvoiceEntity>  recordInvoiceEntity =  importSignService.onlySignImg(buildExportEntity(), ocrMap, scanId);
        
        SignedInvoiceVo invoiceVo = new SignedInvoiceVo();
        
        //xf_name  create_date  scanId check_code
        invoiceVo.setInvoiceCode(ocrMap.get("invoiceCode"));
        invoiceVo.setInvoiceNo(ocrMap.get("invoiceNo"));
        invoiceVo.setCheckCode(ocrMap.get("checkCode"));
        invoiceVo.setInvoiceDate(ocrMap.get("invoiceDate"));
        
        invoiceVo.setGfName(ocrMap.get("gfName"));
        invoiceVo.setGfTaxNo(ocrMap.get("gfTaxNo"));
        invoiceVo.setXfName(ocrMap.get("xfName"));
        invoiceVo.setXfTaxNo(ocrMap.get("xfTaxNo"));
        
        invoiceVo.setTaxAmount(ocrMap.get("taxAmount"));
        invoiceVo.setInvoiceAmount(ocrMap.get("invoiceAmount"));
        invoiceVo.setTotalAmount(ocrMap.get("totalAmount"));
        
        invoiceVo.setInvoiceType(getInvoiceType(ocrMap.get("fplx")));
        
//      01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票

        invoiceVo.setScanId(scanId);
        
       int row = this.scannerSignService.signUseRecord(invoiceVo);
        
       
       String returnMessage = null;
        		
       if(row==1)
       {
    	   returnMessage  = "签收信息保存成功";
       }
       else  if(row==0)
       {
    	   returnMessage  = "签收信息保存失败：重复保存";
       }
       else
       {
    	   returnMessage  = "签收信息保存失败：数据异常";
       }
       
       
        JSONObject json = new JSONObject();
        
        LOGGER.info("图片签收-图片签收保存------------------------------------"+row);

        try {
        	json.put("success", row>0);
			json.put("userId", userId);
			json.put("message", returnMessage);
			//json.put("recordInvoiceEntity", recordInvoiceEntity);
			json.put("scanId", scanId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        return json.toString();
    }
    /**
     * 根据传过来的中文转换成代码
     * @param invoiceType
     * @return
     */
    private String getInvoiceType(String invoiceType)
    {
//      01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
    	if(invoiceType!= null && invoiceType.contains("增值税专用"))
    	{
    		return "01";
    	}
    	if(invoiceType!= null && invoiceType.contains("机动车"))
    	{
    		return "03";
    	}
    	if(invoiceType!= null && invoiceType.contains("增值税普通"))
    	{
    		return "04";
    	}
    	if(invoiceType!= null && invoiceType.contains("电子"))
    	{
    		return "10";
    	}
    	if(invoiceType!= null && invoiceType.contains("卷票"))
    	{
    		return "11";
    	}
    	if(invoiceType!= null && invoiceType.contains("通行费"))
    	{
    		return "14";
    	}
    	
    	return invoiceType;
    }

    /**
     * 构建实体
     * @return 实体
     */
//    private ExportEntity buildExportEntity() {
//        final ExportEntity exportEntity = new ExportEntity();
//        exportEntity.setSchemaLabel(getCurrentUserSchemaLabel());
//        //人员id
//        exportEntity.setUserId(getUserId());
//        //帐号
//        exportEntity.setUserAccount(getUser().getLoginname());
//        //人名
//        exportEntity.setUserName(getUserName());
//        return exportEntity;
//    }
    
   
    /**
     * 构建实体
     * @return 实体
     */
//    private ExportEntity buildExportEntity() {
//        final ExportEntity exportEntity = new ExportEntity();
//        exportEntity.setSchemaLabel(getCurrentUserSchemaLabel());
//        //人员id
//        exportEntity.setUserId(getUserId());
//        //帐号
//        exportEntity.setUserAccount(getUser().getLoginname());
//        //人名
//        exportEntity.setUserName(getUserName());
//        return exportEntity;
//    }
    
}
