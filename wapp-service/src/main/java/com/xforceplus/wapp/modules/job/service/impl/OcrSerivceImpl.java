package com.xforceplus.wapp.modules.job.service.impl;

import com.xforceplus.wapp.modules.job.pojo.OcrDiscAuthorize;
import com.xforceplus.wapp.modules.job.pojo.OcrDiscGlobalInfo;
import com.xforceplus.wapp.modules.job.pojo.OcrDiscernRequest;
import com.xforceplus.wapp.modules.job.service.OcrSerivce;
import com.xforceplus.wapp.modules.job.utils.HttpRequestUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daily.zhang on 2018/04/20.
 */
@PropertySource(value = {"classpath:config.properties"})
@Service("ocrSerivce")
public class OcrSerivceImpl implements OcrSerivce {

    private static Logger logger = LoggerFactory.getLogger(OcrSerivceImpl.class);

    @Value("${ocr.url}")
    private String url;

    @Value("${ocr.appId}")
    private String appId;

    @Value("${ocr.version}")
    private String version;

    @Value("${ocr.enterpriseCode}")
    private String enterpriseCode;

    @Value("${ocr.appKey}")
    private String appKey;

    @Value("${ocr.appSce}")
    private String appSce;

    @Override
    public Map<String, String> discernInvoice(String userid, String uuid, String picBase64) {

        //封装数据
        OcrDiscAuthorize authorize = new OcrDiscAuthorize();
        authorize.setAppKey(appKey);
        authorize.setAppSec(appSce);

        OcrDiscGlobalInfo globalInfo = new OcrDiscGlobalInfo();
        globalInfo.setAppId(appId);
        globalInfo.setVersion(version);
        globalInfo.setEnterpriseCode(enterpriseCode);
        globalInfo.setUserId(userid);
        globalInfo.setUuid(uuid);

        OcrDiscernRequest request = new OcrDiscernRequest();
        request.setPicture(picBase64);
        request.setAuthorize(authorize);
        request.setGlobalInfo(globalInfo);

        String requestStr = JSONObject.fromObject(request).toString();
        Map<String, String> resultMap = new HashMap<>();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("url:{}  param:{}", url, requestStr);
            }
            String responseStr = HttpRequestUtils.httpPost(requestStr, url, 30000);
            if (responseStr.startsWith("{")) {
                JSONObject obj = JSONObject.fromObject(responseStr);
                logger.info("返回的响应报文:{}", obj.toString());
                Map<String, String> returnStateInfo = (Map<String, String>) obj.get("returnStateInfo");
                Map<String, String> invoice = (Map<String, String>) obj.get("invoice");
                resultMap.putAll(returnStateInfo);
                resultMap.putAll(invoice);
            } else {
                logger.error("发票识别服务的接口调用返回信息不是json信息。返回信息:{}", responseStr);
                resultMap.put("returnCode", "9999");
                resultMap.put("returnMessage", responseStr);
            }
        } catch (Exception e) {
            resultMap.put("returnCode", "9999");
            resultMap.put("returnMessage", e.getCause().toString());
            logger.error("url:{}  param:{} 调用服务异常。 cause:{}", url, requestStr, e.getCause(), e);
        }
        return resultMap;
    }
}
