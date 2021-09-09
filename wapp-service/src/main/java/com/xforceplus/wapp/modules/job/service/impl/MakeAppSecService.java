package com.xforceplus.wapp.modules.job.service.impl;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@PropertySource(value = {"classpath:config.properties"})
@Service("makeAppSecService")
public class MakeAppSecService {
    @Value("${secret.key}")
    private String secret_key;
    private Logger logger = LoggerFactory.getLogger(MakeAppSecService.class);
    public String makeAppSec(String secret_id,String appid,String enterpriseCode,String taxperiod,String version,String data,String dataExchangeId ) {
        String appSecKey =secret_key;
        String srcStr =
                "POST/rest/invoice/dii?authorize={\"appSecId\":\""+secret_id+"\"}&globalInfo={\"appId\":\""+appid+"\",\"version\":\""+version+"\",\"interfaceCode\":\""+taxperiod+"\",\"enterpriseCode\":\""+enterpriseCode+"\",\"dataExchangeId\":\""+dataExchangeId+"\"}&"
                        + "data="+data+"";
        SecretKeySpec keySpec = null;
        try {
            keySpec = new SecretKeySpec(appSecKey.getBytes("UTF-8"), "HmacSHA1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            mac.init(keySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] singBytes = new byte[0];
        try {
            singBytes = mac.doFinal(srcStr.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String appSec = Base64.encodeBase64String(singBytes);
        logger.info(taxperiod+"----接口生成的加密串="+appSec+"----");
        return appSec;
    }
}
