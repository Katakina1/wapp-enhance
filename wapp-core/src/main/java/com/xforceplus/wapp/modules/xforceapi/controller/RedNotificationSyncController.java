package com.xforceplus.wapp.modules.xforceapi.controller;

import java.util.Date;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.xforceapi.entity.TXfRednotificationSync;
import com.xforceplus.wapp.modules.xforceapi.entity.TXfRednotificationSyncReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.xforceapi.service.RedNotificationSyncService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH +  "/red-notification-sync")
@Slf4j
public class RedNotificationSyncController {

	@Autowired
	private RedNotificationSyncService redNotificationSyncService;
	
	@ApiOperation(value = "红字信息表同步", notes = "", response = Response.class, tags = {"red-notification-sync",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/sync-deviceun")
	public Response<Object> redNotificationSync(String terminalUn, String deviceUn, Date startDate, Date endDate) {
		log.info("redNotificationSync terminalUn:{},deviceUn:{},startDate:{},endDate:{}", terminalUn, deviceUn, startDate, endDate);
		return redNotificationSyncService.redNotificationSync(terminalUn, deviceUn, startDate, endDate);
	}
	
	@ApiOperation(value = "红字信息表同步", notes = "", response = Response.class, tags = {"red-notification-sync",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/sync")
	public Response<Object> redNotificationSyncTask() {
		log.info("redNotificationSyncTask ");
		return redNotificationSyncService.redNotificationSyncTask();
	}
	
	
	@ApiOperation(value = "获取红字信息表同步结果", notes = "", response = Response.class, tags = {"red-notification-sync",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/get-result")
	public Response<Object> getRedNotificationSyncResult(String serialNo) {
		log.info("getRedNotificationSyncResult serialNo:{}", serialNo);
		return redNotificationSyncService.getRedNotificationSyncResult(serialNo);
	}
	
	@ApiOperation(value = "获取红字信息表同步结果", notes = "", response = Response.class, tags = {"red-notification-sync",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/get-result-task")
	public Response<Object> getRedNotificationSyncResultTask() {
		log.info("getRedNotificationSyncResultTask ");
		return redNotificationSyncService.getRedNotificationSyncResultTask();
	}

	@PostMapping("/getRedNotificationSynctList")
	public R<Page<TXfRednotificationSync>> abnormalListPaged(@RequestBody TXfRednotificationSyncReq request) {
		Page<TXfRednotificationSync> page = redNotificationSyncService.paged(request);
		return R.ok(page);
	}
}
