package com.xforceplus.wapp.client;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Component
public class JanusClient {
    private final HttpClientFactory httpClientFactory;
    @Value("${wapp.integration.action.tax-code}")
    private String taxCodeAction;
    @Value("${wapp.integration.sign.tax-code}")
    private String taxCodeSign;
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

    public Either<String, List<TaxCodeBean>> searchTaxCode(String taxCode, String keyWord) {
        if (StringUtils.isBlank(taxCode) && StringUtils.isBlank(keyWord)) {
            return Either.left("参数不能全为空");
        }
        try {
            defaultHeader.put("uiaSign", taxCodeSign);
            defaultHeader.put("serialNo", taxCode + keyWord);
            HashMap<String, Object> paramMeterMap = Maps.newHashMap();
            if (StringUtils.isNotBlank(taxCode)) {
                paramMeterMap.put("code", taxCode);
            }
            if (StringUtils.isNotBlank(keyWord)) {
                paramMeterMap.put("taxCodeKeyWord", keyWord);
            }
            paramMeterMap.put("appId", "walmart");
            paramMeterMap.put("node", 1);
            log.info("获取中台税编参数:{}", paramMeterMap);
            final String get = httpClientFactory.get(taxCodeAction, paramMeterMap, defaultHeader);
            log.debug("获取中台税编结果:{}", get);
            TaxCodeRsp taxCodeRsp = gson.fromJson(get, TaxCodeRsp.class);
            if (Objects.nonNull(taxCodeRsp) && "TWTXZZ100".equalsIgnoreCase(taxCodeRsp.getCode())) {
                List<TaxCodeBean> codeBeans = taxCodeRsp.getResult().stream().map(TaxCodeRsp.ResultBean::getData)
                        .filter(Objects::nonNull).collect(Collectors.toList());
                return Either.right(codeBeans);
            }
            log.info("获取中台税编错误:{}", get);
            return Either.left(taxCodeRsp.getMessage());
        } catch (IOException e) {
            log.error("获取税编结果异常:" + e.getMessage(), e);
            throw new RRException("获取税编结果异常:" + e.getMessage());
        }
    }
}
