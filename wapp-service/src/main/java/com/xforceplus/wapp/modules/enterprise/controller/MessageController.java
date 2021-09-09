package com.xforceplus.wapp.modules.enterprise.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.download.service.SFtpDownloadService;
import com.xforceplus.wapp.modules.enterprise.service.ExportService;
import com.xforceplus.wapp.modules.export.entity.MessageEntity;
import com.xforceplus.wapp.modules.export.service.IExcelExportService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;

@RestController
public class MessageController extends AbstractController {

	private final ExportService exportService;
	
	private final SFtpDownloadService sFtpDownloadService;
	public MessageController(ExportService exportService, SFtpDownloadService sFtpDownloadService, IExcelExportService excelExportService) {
		this.exportService = exportService;
		this.sFtpDownloadService = sFtpDownloadService;
	}
	
	@RequestMapping("/messageControl/list")
	public R list(@RequestParam Map<String, Object> parmMap) {
		final String schemaLabel = getCurrentUserSchemaLabel();
		
		List<MessageEntity> dataList = new ArrayList<MessageEntity>();
		int totalCount = getMessageControlCount(schemaLabel, parmMap);
		if (totalCount > 0) {
			dataList = exportService.getMessageControl(schemaLabel, parmMap);
		}
		
		return R.ok().put("dataList", dataList).put("totalCount", totalCount);
	}
	
	@RequestMapping("/core/ftp/download")
	public void downloadFile(@RequestParam Map<String, Object> parmMap,  HttpServletResponse response) {
		final String schemaLabel = getCurrentUserSchemaLabel();
		sFtpDownloadService.download(schemaLabel, parmMap, response);
	}
	
	@RequestMapping("/allclickcommit")
	public R allclickcommit() {
		final String schemaLabel = getCurrentUserSchemaLabel();
		int count = exportService.allclickcommit(schemaLabel, getLoginName());
		return R.ok().put("count", count);
	}
	
	@RequestMapping("/message/getMessageCount")
	public R getMessageCount(@RequestParam Map<String, Object> parmMap) {
		final String schemaLabel = getCurrentUserSchemaLabel();
		
		int totalCount = getMessageControlCount(schemaLabel, parmMap);
		return R.ok().put("messageCount", totalCount);
	}
	
	private int getMessageControlCount(String schemaLabel, Map<String, Object> parmMap) {
		parmMap.put("username", getLoginName());
		return exportService.getMessageControlCount(schemaLabel, parmMap);
	}
	
   @Value("${export.websocketUrl}")
   private String websocketUrl;
	
   @RequestMapping("/getWebsocketUrl")
   public R getWebsocketUrl() {
		return R.ok().put("websocketUrl", websocketUrl);
   }
   
   @RequestMapping("message/clickcommit")
   public R clickcommit(@RequestBody Integer id) {
	   final String schemaLabel = getCurrentUserSchemaLabel();
	   int result = exportService.clickcommit(schemaLabel, id);
	   return (result > 0) ? R.ok() : R.error(); 
   }
}
