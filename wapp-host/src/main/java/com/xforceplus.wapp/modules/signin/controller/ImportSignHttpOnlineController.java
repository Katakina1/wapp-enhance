package com.xforceplus.wapp.modules.signin.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

//import org.json.JSONObject;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.signin.entity.InvoiceScan;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.service.ImportSignService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * 图片上传
 * @author jingsong.mao
 * @date 7/30/2018
 */
@Api(tags="客户端-图片上传")
@RestController
@RequestMapping("/onlineUpload")
public class ImportSignHttpOnlineController extends AbstractController {

    private final static Logger LOGGER = getLogger(ImportSignHttpOnlineController.class);

    private final ImportSignService importSignService;

    @Autowired
    public ImportSignHttpOnlineController(ImportSignService importSignService) {
        this.importSignService = importSignService;
    } 
    /**
     * 图片上传
     */
    @ApiOperation("图片上传")
    @SysLog("图片上传")
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String importSignImgHttp(String userId, String token,String ocrMapJson, @RequestParam("file") MultipartFile file) {
       logger.info("发票信息："+ocrMapJson);
    	String jsonString =  ocrMapJson;

        JSONObject jasonObject = JSONObject.fromObject(jsonString);
        
        Map<String, String> ocrMap = (Map)jasonObject;
        String scanId = file.getOriginalFilename();//scanId就是客户端传过来的文件名称,去掉后面的.jpg

        try {
            scanId = URLDecoder.decode(scanId.substring(0,  scanId.lastIndexOf('.')),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ocrMap.put("scanId",scanId);
        LOGGER.info("在线上传------------------------------------"+userId);
    	

        JSONObject json = new JSONObject();
        
        String scanPoint = ocrMap.get("scanPoint");
        
        String billtypeCode = ocrMap.get("billtypeCode");
        
//      //校验扫描点是否合法
//        Integer smdSize=importSignService.checkScanPoint(getCurrentUserSchemaLabel(), userId, scanPoint);
//        if( smdSize<1)
//       {
//        	json.put("uuid", null);
//			json.put("success", false);
//			json.put("message", "图片在线上传失败，扫描点非法，未执行上传操作。请重新登录获取");
//			json.put("userId", userId);
//			json.put("fileName", file.getOriginalFilename());
//		    return json.toString();
//       }
      
//        //校验票据类型是否合法
//        Integer ywlxSize=importSignService.checkbilltypeCode(getCurrentUserSchemaLabel(), userId, billtypeCode);
//       if(ywlxSize<1)
//       {
//    	   json.put("uuid", null);
//			json.put("success", false);
//			json.put("message", "图片在线上传失败，业务类型非法，未执行上传操作。请重新登录获取");
//			json.put("userId", userId);
//			json.put("fileName", file.getOriginalFilename());
//			return json.toString();
//       }
       
        //执行上传
        try {
        	String uuid = importSignService.excuteUpload(buildExportEntity(), file, ocrMap);
			json.put("uuid", uuid);
			json.put("success", uuid!=null && !uuid.equals(""));
			json.put("message", (uuid!=null && !uuid.equals(""))?"签收成功":"签收失败");
			json.put("userId", userId);
			json.put("fileName", file.getOriginalFilename());
		}catch (Exception e) {
			e.printStackTrace();
			json.put("message", "签收失败：服务器发生异常，请联系管理员！\n异常原因：" + e.getMessage());
			json.put("uuid", "");
			json.put("success", "false");
			json.put("userId", userId);
			json.put("fileName", file.getOriginalFilename());
		}



        return json.toString();
    }



    @SysLog("扫描修改签收")
    @RequestMapping("/updateInvoice")
    @ResponseBody
    public R updateInvoice(@RequestBody String requestBody, HttpServletRequest request) {

        JSONObject jsonObject = JSONObject.fromObject(requestBody);
        Map<String,Object> invoices=jsonObject;
        final ExportEntity exportEntity = new ExportEntity();

        exportEntity.setSchemaLabel((String) invoices.get("exportEntitySchemaLabel"));
        //人员id
        exportEntity.setUserId(Long.valueOf((Integer) invoices.get("exportEntityUserId")));
        //帐号
        exportEntity.setUserAccount((String) invoices.get("exportEntityUserAccount"));
        //人名
        exportEntity.setUserName((String) invoices.get("exportEntityUserName"));
        try {
            final Map<String,Object> recordInvoiceEntityMap = importSignService.getUpdateRecordInvoiceEntity(exportEntity,invoices);
            return R.ok().put("page",recordInvoiceEntityMap.get("list"));
        } catch ( RRException e) {
            logger.error("扫描签收失败，excel失败:{}", e);
            return R.error(9999, e.getMessage());
        }

//        //返回请求结果
//
//        JSONObject result= new JSONObject();
//
//        result.put("success", "true");

//        return null;

    }


//    public R updateInvoice(Map<String, Object> invoices){
//        System.out.println("");
//        //通过id获取修改前数据，并构建修改后要进行查验的内容
//
////        try {
////            final Map<String,Object> recordInvoiceEntityMap = importSignService.getUpdateRecordInvoiceEntity(exportEntity,invoices);
////            return R.ok().put("page",recordInvoiceEntityMap);
////        } catch ( RRException e) {
////            logger.error("扫描签收失败，excel失败:{}", e);
////            return R.error(9999, e.getMessage());
////        }
//        return null;
//    }








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
