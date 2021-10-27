package com.xforceplus.wapp.client;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.model.taxware.GetTerminalResponse;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class JanusClient {
    private final HttpClientFactory httpClientFactory;
    @Value("${wapp.integration.action.tax-code}")
    private String taxCode;
    @Value("${wapp.integration.tenant-id:1203939049971830784}")
    public String tenantId;
    private final Map<String, String> defaultHeader = new HashMap<>();
    private final Gson gson = new Gson();

    public JanusClient(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    @PostConstruct
    public void init() {
        defaultHeader.put("rpcType", "http");
        defaultHeader.put("tenant-id", tenantId);
        defaultHeader.put("tenantId", tenantId);
        defaultHeader.put("tenantCode", tenantId);
        defaultHeader.put("accept-encoding", "");
    }

    public Either<String, List<TaxCodeRsp.ResultBean>> searchTaxCode(String queryText) {
        try {
            HashMap<String, Object> paramMeterMap = Maps.newHashMap();
            paramMeterMap.put("queryText", queryText);
            paramMeterMap.put("appId", 20);
            final String get = httpClientFactory.get(taxCode, paramMeterMap, defaultHeader);
            log.info("获取税编结果:{}", get);
            TaxCodeRsp taxCodeRsp = gson.fromJson(get, TaxCodeRsp.class);
            if ("".equalsIgnoreCase(taxCodeRsp.getCode())) {
                return Either.right(taxCodeRsp.getResult());
            }
            return Either.left(taxCodeRsp.getMessage());
        } catch (IOException e) {
            log.error("获取税编结果异常:" + e.getMessage(), e);
            throw new RRException("获取税编结果异常:" + e.getMessage());
        }
    }
}
