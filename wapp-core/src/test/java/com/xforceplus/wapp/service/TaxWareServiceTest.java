package com.xforceplus.wapp.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.rednotification.model.taxware.RedMessage;
import com.xforceplus.wapp.modules.rednotification.model.taxware.RedRevokeMessageResult;
import com.xforceplus.wapp.modules.rednotification.service.TaxWareService;

public class TaxWareServiceTest extends BaseUnitTest {

	@Autowired
	private TaxWareService taxWareService;
	
	@Test
	public void test_applyRedNotificationIntegrationResult_success() {
		String obj = "{\r\n" + 
				"  \"redApplyResultList\": [\r\n" + 
				"    {\r\n" + 
				"      \"applyDate\": \"20220917\",\r\n" + 
				"      \"diskNo\": \"1\",\r\n" + 
				"      \"pid\": \"122494803921088512\",\r\n" + 
				"      \"processFlag\": \"1\",\r\n" + 
				"      \"processRemark\": \"红字信息表上传成功\",\r\n" + 
				"      \"redNotificationNo\": \"4420220917090473\",\r\n" + 
				"      \"requestBillNo\": \"491000091202202209170904\",\r\n" + 
				"      \"statusCode\": \"0000\",\r\n" + 
				"      \"statusMsg\": \"成功\"\r\n" + 
				"    }\r\n" + 
				"  ],\r\n" + 
				"  \"serialNo\": \"122494805363929088\"\r\n" + 
				"}";
		RedMessage redMessage = JsonUtil.fromJson(obj, RedMessage.class);
		taxWareService.handle(redMessage);
	}
	
	@Test
	public void test_applyRedNotificationIntegrationResult_fail() {
		String obj = "{\"redApplyResultList\":[{\"applyDate\":\"20220615\",\"pid\":\"88452918193823744\",\"processFlag\":\"1\",\"processRemark\":\"红字信息表上传成功\",\"redNotificationNo\":\"4403042206162251\",\"requestBillNo\":\"661923652161220615153853\",\"statusCode\":\"TZD0000\",\"statusMsg\":\"审核通过\"},{\"applyDate\":\"20220618\",\"pid\":\"89362727738941440\",\"processFlag\":\"1\",\"processRemark\":\"红字信息表上传成功\",\"redNotificationNo\":\"4403042206198902\",\"requestBillNo\":\"661923652161220618035409\",\"statusCode\":\"TZD0074\",\"statusMsg\":\"已核销\"},{\"applyDate\":\"20220727\",\"pid\":\"93142012924768256\",\"processFlag\":\"1\",\"processRemark\":\"红字信息表上传成功\",\"redNotificationNo\":\"4403042207313160\",\"requestBillNo\":\"661923652161220727181245\",\"statusCode\":\"TZD0000\",\"statusMsg\":\"审核通过\"},{\"applyDate\":\"20220628\",\"pid\":\"93142370707288064\",\"processFlag\":\"0\",\"processRemark\":\"第01行明细中，货物（劳务服务）金额必须大于税额\",\"redNotificationNo\":\" \",\"requestBillNo\":\"661923652161220628234944\",\"statusCode\":\"B80045\",\"statusMsg\":\"第01行明细中，货物（劳务服务）金额必须大于税额\"},{\"pid\":\"102078812938055680\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923652161220723060406\"},{\"pid\":\"102084163880103936\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923626405220727001207,661923652161220726102855\"},{\"pid\":\"102084165176143872\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923626405220727001208,661923652161220726102921\"},{\"pid\":\"102085045216620544\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923652161220726121237\"},{\"pid\":\"102085121414541312\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923652161220726121143\"},{\"pid\":\"102085124182781952\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923652161220726121328\"},{\"pid\":\"102085259876904960\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923652161220726121650\"},{\"pid\":\"102087141492011008\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923652161220726162126\"},{\"pid\":\"102087335931555840\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923652161220726162917\"},{\"pid\":\"102087807904002048\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923652161220727033537\"},{\"pid\":\"102088147558739968\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923652161220726223324\"},{\"pid\":\"102089046209343488\",\"processFlag\":\"0\",\"processRemark\":\"未处理，上一张处理失败\",\"requestBillNo\":\"661923652161220727020512\"}],\"serialNo\":\"103711977104822272\"}";
		RedMessage redMessage = JsonUtil.fromJson(obj, RedMessage.class);
		taxWareService.handle(redMessage);
	}
	
	@Test
	public void test_rollbackRedNotificationIntegrationResultHandler_success() {
		String obj = "{\"code\":\"TXWR000000\",\"message\":\"成功\",\"result\":{\"redNotificationNo\":\"4403042207264985\",\"serialNo\":\"103726387634458624\"}}";
		RedRevokeMessageResult redMessage = JsonUtil.fromJson(obj, RedRevokeMessageResult.class);
		taxWareService.handleRollBack(redMessage);
	}
	
	@Test
	public void test_rollbackRedNotificationIntegrationResultHandler_fail() {
		String obj = "{\"code\":\"TXWRCT451124\",\"message\":\"红字信息表撤销失败：红字信息表撤销失败!错误码：B80048\\r\\n失败原因：信息表状态为：已核销 不允许撤销(TZD0000-审核通过) \",\"result\":{\"redNotificationNo\":\"4403042206191411\",\"serialNo\":\"103690759681167360\"}}";
		RedRevokeMessageResult redMessage = JsonUtil.fromJson(obj, RedRevokeMessageResult.class);
		taxWareService.handleRollBack(redMessage);
	}
}
