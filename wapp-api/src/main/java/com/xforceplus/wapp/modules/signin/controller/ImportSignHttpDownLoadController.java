package com.xforceplus.wapp.modules.signin.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;

//import org.json.JSONObject;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.modules.signin.service.ImportSignService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 图片下载
 * @author jingsong.mao
 * @date 7/30/2018
 */
@Api(tags="客户端-图片下载")
@RestController
@RequestMapping("/onlineDownload")
public class ImportSignHttpDownLoadController extends AbstractController {

    private final static Logger LOGGER = getLogger(ImportSignHttpDownLoadController.class);

    private final ImportSignService importSignService;

    @Autowired
    public ImportSignHttpDownLoadController(ImportSignService importSignService) {
        this.importSignService = importSignService;
    } 
    
    
    
    
    @ApiOperation("图片下载")
    @SysLog("图片下载")
    @ResponseBody
    @RequestMapping(method = {RequestMethod.POST})
    public String getInvoiceImage(String userId, String token,String uuid) {
    	
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("uuid", uuid);
    	

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);
        
        LOGGER.info("获取图片的请求参数为:{}", params);
        
        String encodeBase64String =  importSignService.getInvoiceImage(params);

        JSONObject json = new JSONObject();
        
        
        LOGGER.info("获取图片------------------------------------"+userId);

        try {
        	json.put("success", encodeBase64String!=null && !encodeBase64String.equals(""));
			json.put("message", (encodeBase64String!=null && !encodeBase64String.equals(""))?"图片获取成功":"图片获取失败");
			json.put("userId", userId);
			json.put("encodeBase64String", encodeBase64String);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        return json.toString();
    }

}
