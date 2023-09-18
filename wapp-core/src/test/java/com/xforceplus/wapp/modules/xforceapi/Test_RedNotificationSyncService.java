package com.xforceplus.wapp.modules.xforceapi;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.xforceapi.service.RedNotificationSyncService;

public class Test_RedNotificationSyncService extends BaseUnitTest{

	@Autowired
	private RedNotificationSyncService redNotificationSyncService;
	
	
	@Test
	public void test_redNotificationSync() {
		Date currentDate = DateUtils.getNowDateShort();
		//Object result = redNotificationSyncService.redNotificationSync("", "8TKSQZTM", DateUtils.addDate(currentDate, -8), DateUtils.addDate(currentDate, -5));
		//System.err.println(JSON.toJSONString(result));
		Object result = redNotificationSyncService.redNotificationSync("", "JIOCPEUF", DateUtils.addDate(currentDate, -8), DateUtils.addDate(currentDate, -5));
		System.err.println(JSON.toJSONString(result));
	}
	
	@Test
	public void test_getRedNotificationSyncResult() {
		String serialNo = "5F14A530921C0F0B6DA412CB2E03D3A9-0221";
		redNotificationSyncService.getRedNotificationSyncResult(serialNo);
	}
	
	@Test
	public void test_getRedNotificationSyncResultTask() {
		Object result = redNotificationSyncService.getRedNotificationSyncResultTask();
		System.err.println(JSON.toJSONString(result));
	}
	
	
}
