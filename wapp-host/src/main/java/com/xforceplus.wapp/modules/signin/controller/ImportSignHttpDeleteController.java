package com.xforceplus.wapp.modules.signin.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.modules.signin.service.ImportSignService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 图片删除
 * @author jingsong.mao
 * @date 7/30/2018
 */
@Api(tags="客户端-图片删除")
@RestController
@RequestMapping("/onlineDelete")
public class ImportSignHttpDeleteController extends AbstractController {

    private final static Logger LOGGER = getLogger(ImportSignHttpDeleteController.class);

    private final ImportSignService importSignService;

    @Autowired
    public ImportSignHttpDeleteController(ImportSignService importSignService) {
        this.importSignService = importSignService;
    } 
    
    
   
    
    @ApiOperation("图片删除")
    @SysLog("图片删除")
    @ResponseBody
    @RequestMapping(method = {RequestMethod.POST})
    public String deleteSignInImg(String userId, String token,String uuid) {
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("uuid", uuid);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);
        
        LOGGER.info("删除图片的请求参数为:{}", params);
        
        String invoiceImage =  importSignService.deleteInvoiceImage(params);

        JSONObject json = new JSONObject();
        
        LOGGER.info("删除图片------------------------------------"+userId);

        try {
        	json.put("success", invoiceImage!=null && !invoiceImage.equals(""));
        	json.put("message", (invoiceImage!=null && !invoiceImage.equals(""))?"图片删除成功":"图片删除失败");
			json.put("userId", userId);
			json.put("invoiceImage", invoiceImage);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return json.toString();
    }
    
}
