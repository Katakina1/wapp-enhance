package com.xforceplus.wapp.modules.backfill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.apollo.msg.SealedMessage.Header;
import com.xforceplus.apollo.msg.SealedMessage.Payload;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.handle.IntegrationResultHandler;
import com.xforceplus.wapp.modules.backfill.dto.AnalysisXmlResult;
import com.xforceplus.wapp.modules.backfill.dto.TaxWareResponse;
import com.xforceplus.wapp.modules.backfill.events.XmlToPdfEvent;
import com.xforceplus.wapp.modules.backfill.model.VerificationBack;
import com.xforceplus.wapp.modules.backfill.model.VerificationRequest;
import com.xforceplus.wapp.modules.backfill.model.VerificationResponse;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    @Value("${wapp.integration.action.verification-xml}")
    private String verificationXml;

    private final Map<String, String> defaultHeader;

    @Value("${wapp.integration.tenant-code}")
    private String tenantCode;

    private static final String VERIFICATION_LEVEL = "1";

    @Value("${wapp.integration.customer-no}")
    private String customerNo;

    @Value("${wapp.integration.action.downLoadAction}")
    private String downLoadAction;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    @Lazy
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

    public void analysisAndVerify(byte[] is, String batchNo,String vendorId, String uploadFileId,
                                  Consumer<String> taskIdC, Consumer<String> errC) {
        try {
            this.analysisAndVerify(is, batchNo, vendorId, uploadFileId).ifPresent(taskIdC);
        } catch (EnhanceRuntimeException e) {
            errC.accept(e.getMessage());
        }
    }

    public Optional<String> analysisAndVerify(byte[] is, String batchNo, String vendorId, String uploadFileId) {
        //解析xml
        AnalysisXmlResult result = this.analysis(is, batchNo, vendorId, uploadFileId);
        //验签成功
        if (result != null) {
            final AnalysisXmlResult.InvoiceMainDTO invoiceMain = result.getInvoiceMain();
            VerificationRequest verificationRequest = new VerificationRequest();
            verificationRequest.setInvoiceNo(invoiceMain.getInvoiceNo());
            verificationRequest.setPaperDrewDate(invoiceMain.getPaperDrewDate());
            verificationRequest.setAmount(invoiceMain.getAmountWithoutTax());
            verificationRequest.setAmountWithTax(invoiceMain.getAmountWithTax());
            VerificationResponse verificationResponse = this.verify(verificationRequest);
            if (verificationResponse.isOK()) {
                return Optional.ofNullable(verificationResponse.getResult());
            }
            throw new EnhanceRuntimeException("验真失败:" + verificationResponse.getMessage());
        }
        return Optional.empty();
    }

    public AnalysisXmlResult analysis(byte[] is, String batchNo, String vendorId, String uploadFileId) {
        try {
            String base64 = Base64.getEncoder().encodeToString(is);
            Map<String, String> header = new HashMap<>(defaultHeader);
            header.put("serialNo", batchNo);

            JSONObject body = new JSONObject();
            body.put("tenantCode", tenantCode);
            body.put("xmlEncode", base64);

            final String post = httpClientFactory.post(verificationXml, header, body.toJSONString(), "");
            log.info("解析xml文件结果:{}", post);
            TaxWareResponse taxWareResponse = JsonUtil.fromJson(post, TaxWareResponse.class);
            if (taxWareResponse.isOK()) {
                //抛事件处理 xml结构生成pdf
                applicationContext.publishEvent(new XmlToPdfEvent(vendorId, uploadFileId, taxWareResponse.getResult()));
                return taxWareResponse.getResult();
            }
            throw new EnhanceRuntimeException("解析xml文件失败:" + taxWareResponse.getMessage());
        } catch (IOException e) {
            log.error("解析xml文件失败:" + e.getMessage(), e);
            throw new EnhanceRuntimeException("解析xml文件失败:" + e.getMessage());
        }
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
            Map<String,String> header=new HashMap<>(defaultHeader);
            header.put("serialNo",invoiceMain.getInvoiceCode()+invoiceMain.getInvoiceNo());
            final String post = httpClientFactory.post(verifyAction, header, JSONObject.toJSONString(invoiceMain), "");
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
            if(StringUtils.isEmpty(baseUrl)){
                return null;
            }
            paramMeterMap.put("ossUrl",baseUrl);
            paramMeterMap.put("type","1");
            Map<String,String> header=new HashMap<>(defaultHeader);
            header.put("serialNo",uuId);
            final String get = httpClientFactory.get(downLoadAction,paramMeterMap,header);
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
            Map<String,String> header=new HashMap<>(defaultHeader);
            header.put("serialNo",url);
            final String get = httpClientFactory.get(downLoadAction,paramMeterMap,header);
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
