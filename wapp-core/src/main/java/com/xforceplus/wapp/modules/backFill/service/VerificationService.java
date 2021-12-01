package com.xforceplus.wapp.modules.backFill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.apollo.msg.SealedMessage.Header;
import com.xforceplus.apollo.msg.SealedMessage.Payload;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.handle.IntegrationResultHandler;
import com.xforceplus.wapp.modules.backFill.model.VerificationBack;
import com.xforceplus.wapp.modules.backFill.model.VerificationRequest;
import com.xforceplus.wapp.modules.backFill.model.VerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 验真相关业务类
 * 1. 发起验真
 * 2. 接收验真结果
 *
 * @author malong@xforceplus.com
 * @program wapp-web
 * @create 2021-09-15 16:46
 **/
@Service
@Slf4j
public class VerificationService implements IntegrationResultHandler {
    public static final String REQUEST_NAME = "invoiceVerifyUploadResult";

    @Autowired
    private HttpClientFactory httpClientFactory;

    @Value("${wapp.integration.action.verification}")
    private String verifyAction;

    private final Map<String, String> defaultHeader;

    @Value("${wapp.integration.tenant-code}")
    private String tenantCode;

    private static final String VERIFICATION_LEVEL = "1";

    @Value("${wapp.integration.customer-no}")
    private String customerNo;

    @Value("${wapp.integration.action.downLoadAction}")
    private String downLoadAction;

    @Autowired
    private EInvoiceMatchService eInvoiceMatchService;

    private static String KEY = "NOBUSINESS_SIGN_";
    @Autowired
    private RedisTemplate redisTemplate;

    public VerificationService(@Value("${wapp.integration.tenant-id}")
                                     String tenantId) {
        defaultHeader =  new HashMap<>();
        defaultHeader.put("rpcType", "http");
        defaultHeader.put("x-app-client", "janus");
        defaultHeader.put("tenant-id", tenantId);
        defaultHeader.put("accept-encoding","");
    }

    public VerificationResponse verify(VerificationRequest request) {
        request.setTenantCode(tenantCode);
        request.setCustomerNo(customerNo);
        request.setYzLevel(VERIFICATION_LEVEL);
        return doVerify(request);
    }

    /**
     * 发票验真
     *
     * @param invoices 发票列表
     */
    public void verify(List<VerificationRequest> invoices) {

        for (VerificationRequest invoice : invoices) {
            verify(invoice);
        }
    }

    private VerificationResponse doVerify(VerificationRequest invoiceMain) {

        try {
            defaultHeader.put("serialNo",invoiceMain.getInvoiceCode()+invoiceMain.getInvoiceNo());
            final String post = httpClientFactory.post(verifyAction, defaultHeader, JSONObject.toJSONString(invoiceMain), "");
            log.info("验真结果:{}", post);
            return JSONObject.parseObject(post, VerificationResponse.class);
        } catch (IOException e) {
            log.error("验真发起失败:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("验真发起失败:" + e.getMessage());
        }
    }

    public String getBase64ByUrl(String uuId) {

        try {
            HashMap<String, Object> paramMeterMap = Maps.newHashMap();
            String baseUrl=(String)redisTemplate.opsForValue().get(KEY+uuId);
            paramMeterMap.put("ossUrl",baseUrl);
            paramMeterMap.put("type","1");
            defaultHeader.put("serialNo",uuId);
            final String get = httpClientFactory.get(downLoadAction,paramMeterMap,defaultHeader);
            return get;
        } catch (IOException e) {
            log.error("获取下载结果:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("获取下载结果:" + e.getMessage());
        }
    }

    public String getBase64ByRealUrl(String url) {

        try {
            HashMap<String, Object> paramMeterMap = Maps.newHashMap();
            paramMeterMap.put("ossUrl",url);
            paramMeterMap.put("type","1");
            defaultHeader.put("serialNo","123456");
            final String get = httpClientFactory.get(downLoadAction,paramMeterMap,defaultHeader);
            return get;
        } catch (IOException e) {
            log.error("获取下载结果:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("获取下载结果:" + e.getMessage());
        }
    }

    /**
     * 业务流程：上传-》云识别-》结果通知-》发票验真-》结果通知-》存储（记录表、发票表）
     */
    @Override
    public boolean handle(SealedMessage sealedMessage) {
        Header header = sealedMessage.getHeader();

        Payload payload = sealedMessage.getPayload();

        log.info("发票验真异步结果>>>>SealedMessage.header:{},payload.getObj: {}", header, payload.getObj());

        TypeReference<VerificationBack> typeRef = new TypeReference<VerificationBack>() {
        };

        VerificationBack verificationBack = JSON.parseObject(payload.getObj().toString(), typeRef);
        eInvoiceMatchService.matchResultAfterVerify(verificationBack,header);

        return true;
    }


    @Override
    public String requestName() {
        return REQUEST_NAME;
    }
}
