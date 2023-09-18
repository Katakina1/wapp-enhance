package com.xforceplus.wapp.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 签名工具类
 */
@Slf4j
public class SignUtils {

    // 创建BMS签名
    public static String getBMSSign(String timestamp, String jsonParam, String appSecret) {
        return getSign("wapp", "wapp", timestamp, "json", "1.0", jsonParam, appSecret);
    }

    private static String getSign(String appName, String source, String timestamp, String format, String version, String param, String appSecret) {
        param = JSON.toJSONString(JSON.parseObject(param), SerializerFeature.MapSortField);
        String result = appName + source + timestamp + format + version + param;
        return getSignBySHA512(result, appSecret);
    }

    public static String getSignBySHA512(String param, String appSecret) {
        log.info("param:" + param);
        StringBuilder resultStr = new StringBuilder("");
        resultStr.append(appSecret).append(param).append(appSecret);
        log.info("encrypt param str:" + resultStr.toString());
        String sign = hashValue(resultStr.toString()).toUpperCase();
        log.info("encrypt sign:" + sign);
        return sign;
    }

    public static String hashValue(String value) {
        MessageDigest sha512 = null;
        try {
            sha512 = MessageDigest.getInstance("SHA-512");
            sha512.update(value.getBytes());
        } catch (NoSuchAlgorithmException e) {
            log.error("no such Algorithm:",e);
        }
        return convertByteToHex(sha512.digest());
    }

    public static String convertByteToHex(byte[] data) {
        StringBuilder hexData = new StringBuilder();
        for (int byteIndex = 0; byteIndex < data.length; byteIndex++) {
            hexData.append(Integer.toString((data[byteIndex] & 0xff) + 0x100, 16).substring(1));
        }
        return hexData.toString();
    }
}