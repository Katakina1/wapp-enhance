package com.xforceplus.wapp.modules.rednotification.service;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.model.taxware.ApplyRequest;
import com.xforceplus.wapp.modules.rednotification.model.taxware.GetTerminalResponse;
import com.xforceplus.wapp.modules.rednotification.model.taxware.TaxWareResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用税件
 */
@Service
@Slf4j
public class TaxWareService {

    @Autowired
    private HttpClientFactory httpClientFactory;

    @Value("${wapp.integration.action.terminals}")
    private String getTerminalAction;

    @Value("${wapp.integration.action.rednotification}")
    private String applyRedAction;

    private final Map<String, String> defaultHeader;


    private static final String VERIFICATION_LEVEL = "1";


    public TaxWareService(@Value("${wapp.xf.tenant-id:1203939049971830784}")
                                           String tenantId) {
        defaultHeader =  new HashMap<>();
        defaultHeader.put("rpcType", "http");
//        defaultHeader.put("x-app-client", "janus");
        defaultHeader.put("tenant-id", tenantId);
        defaultHeader.put("tenantCode", tenantId);
        defaultHeader.put("accept-encoding","");
    }

    Gson gson = new Gson();

    public GetTerminalResponse getTerminal(String taxNo) {
        try {
            HashMap<String, Object> paramMeterMap = Maps.newHashMap();
            paramMeterMap.put("taxNo",taxNo);
            final String get = httpClientFactory.get(getTerminalAction,paramMeterMap,defaultHeader);
            log.info("获取终端结果:{}", get);
            return gson.fromJson(get, GetTerminalResponse.class);
        } catch (IOException e) {
            log.error("获取终端结果发起失败:" + e.getMessage(), e);
            throw new RRException("获取终端结果发起失败:" + e.getMessage());
        }
    }

    public TaxWareResponse applyRedInfo(ApplyRequest applyRequest) {
        try {
            String reqJson = gson.toJson(applyRequest);
            final String post = httpClientFactory.post(applyRedAction,defaultHeader,reqJson,"");
            log.info("申请结果:{}", post);
            return gson.fromJson(post, TaxWareResponse.class);
        } catch (IOException e) {
            log.error("申请发起失败:" + e.getMessage(), e);
            throw new RRException("申请发起失败:" + e.getMessage());
        }
    }


}
