package com.xforceplus.wapp.modules.xforceapi.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.xforceapi.HttpClientUtils;
import com.xforceplus.wapp.modules.xforceapi.model.RedNotificationSyncRequest;
import com.xforceplus.wapp.modules.xforceapi.model.RedNotificationSyncResponse;
import com.xforceplus.wapp.modules.xforceapi.model.RedNotificationSyncResultResponse;
import com.xforceplus.wapp.modules.xforceapi.service.JanusApiService;

@Service
public class JanusApiServiceImpl implements JanusApiService{
	
	private final static Logger log = LoggerFactory.getLogger(JanusApiServiceImpl.class);
	
	@Value("${wapp.integration.host.http}")
    private String janusPath;
	@Value("${wapp.integration.tenant-id}")
    private String tenantId;
	@Value("${wapp.integration.tenant-code}")
    private String tenantCode;
    @Value("${wapp.integration.customer-no}")
    private String customerNo;
    @Value("${wapp.integration.authentication}")
    private String authentication;
    @Value("${wapp.integration.sign.tax-code}")
    private String taxCodeSign;

	@Override
	public RedNotificationSyncResponse redNotificationSync(RedNotificationSyncRequest requestParam) {
		String url = "https://janus.xforceplus.com";
		String action = "5F14A530921C0F0B6DA412CB2E03D3A9";
		String sign = "44aaabcd484a18c5bff669d8a8b62200";
		Map<String, String> header = bulidHeaders(action, requestParam.getSerialNo(), sign);
		// 默认取集团代码
		if (StringUtils.isBlank(requestParam.getTenantCode())) {
			requestParam.setTenantCode(tenantId);
		}
		log.info("redNotificationSync action:{},sign:{}, param:{}, headers:{}", action, sign, JSON.toJSONString(requestParam), JSON.toJSONString(header));
		String result = HttpClientUtils.postJson(url, JSON.toJSONString(requestParam), header);
		//String result = "{\"code\":\"TXWR000000\",\"message\":\"成功\",\"traceId\":null,\"result\":{\"serialNo\":\"5F14A530921C0F0B6DA412CB2E03D3A9-0221\"}}";
		log.info("redNotificationSync serialNo:{}, result:{}", requestParam.getSerialNo(), result);
		return JSON.parseObject(result, RedNotificationSyncResponse.class);
	}

	@Override
	public RedNotificationSyncResultResponse getRedNotificationSyncResult(String serialNo) {
		String url = "https://janus.xforceplus.com/";
		String action = "C0AC3513EDF5DF862D03B142BDC12D6E";
		String sign = "44aaabcd484a18c5bff669d8a8b62200";
		Map<String, String> header = bulidHeaders(action, serialNo, sign);
		Map<String, String> params = new HashMap<String, String>();
		params.put("serialNo", serialNo);
		log.info("redNotificationSync action:{},sign:{}, param:{}, header:{}", action, sign, JSON.toJSONString(params), JSON.toJSONString(header));
		String result = HttpClientUtils.get(url, header, params);
		log.info("redNotificationSync serialNo:{}, result:{}", serialNo, result);
		return JSON.parseObject(result, RedNotificationSyncResultResponse.class);
	}
	
	protected Map<String, String> bulidHeaders(String action, String serialNo, String sign){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("action", action);
		headers.put("uiaSign", sign);
		
		headers.put("tenant-id", tenantId);
		headers.put("tenantId", tenantId);
		headers.put("tenantCode", tenantCode);
		headers.put("Authentication", authentication);
//		headers.put("Authentication", "walmart-red20211130094319792199387");
//		headers.put("tenant-id", "5938770886372409344");
//		headers.put("tenantId", "5938770886372409344");
//		headers.put("tenantCode", "5938770886372409344");
		
		headers.put("serialNo", serialNo);
		headers.put("timestamp", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		headers.put("accept", "application/json");
        headers.put("Accept-Encoding", "deflate");
        headers.put("Content-Type", "application/json");
        headers.put("rpcType", "http");
		return headers;
	}

}
