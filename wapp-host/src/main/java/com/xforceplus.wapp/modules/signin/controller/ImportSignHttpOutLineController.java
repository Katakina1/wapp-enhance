package com.xforceplus.wapp.modules.signin.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.service.ImportSignService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.modules.sys.service.ShiroService;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 导入签收
 * @author jingsong.mao
 * @date 7/30/2018
 */
@RestController
@RequestMapping("/api")
public class ImportSignHttpOutLineController extends AbstractController {

    private final static Logger LOGGER = getLogger(ImportSignHttpOutLineController.class);

    private final ImportSignService importSignService;
    
    private final ShiroService shiroService;

    @Value("${mycat.default_schema_label}")
    private String mycatDefaultSchemaLabel;

    @Autowired
    public ImportSignHttpOutLineController(ImportSignService importSignService,ShiroService shiroService) {
        this.importSignService = importSignService;
        this.shiroService = shiroService;
    } 

   // @SysLog("图片上传")
    @ResponseBody
    @RequestMapping(value ="/imgSignHttp" , method = RequestMethod.POST)
    public String importSignImgHttp(HttpServletResponse response,HttpServletRequest request, @RequestParam("file") MultipartFile file) {
       

    	 String jsonString =  request.getParameter("ocrMapJson");
    	 
    	 //获取request里的参数
         String userId=request.getParameter("userId");
         
         LOGGER.info("离线上传------------------------------------"+userId);
         

         JSONObject jasonObject = JSONObject.fromObject(jsonString);
         
         Map<String, String> ocrMap = (Map)jasonObject;
    	
    	 //查询用户信息
        UserEntity userd = shiroService.queryUser(mycatDefaultSchemaLabel, Long.valueOf(request.getParameter("userId")));
        
        ExportEntity exportEntity  = buildExportEntityOutLine(userd.getSchemaLabel(),userd.getUserid().longValue(),userd.getUsername());
        
        JSONObject json = new JSONObject();
        
        try {
        	String uuid = importSignService.onlyUploadImg(exportEntity,file, ocrMap);
			json.put("uuid", uuid);
			json.put("success", uuid!=null && !uuid.equals(""));
			json.put("message", (uuid!=null && !uuid.equals(""))?"图片离线上传成功":"sftp服务异常，图片离线上传失败");
			json.put("userId", userId);
			json.put("fileName", file.getOriginalFilename());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
       
        return json.toString();
    }
    /**
     * 构建实体
     * @return 实体
     */
    private ExportEntity buildExportEntityOutLine(String schemaLabel,Long userId,String userName) {
        final ExportEntity exportEntity = new ExportEntity();
        exportEntity.setSchemaLabel(schemaLabel);
        //人员id
        exportEntity.setUserId(userId);
        //帐号
        exportEntity.setUserAccount(userName);
        //人名
        exportEntity.setUserName(userName);
        return exportEntity;
    }
}
