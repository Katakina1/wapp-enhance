package com.xforceplus.wapp.modules.entryaccount.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @Author: ChenHang
 * @Date: 2023/6/30 10:20
 */
@Slf4j
public class SignUtil {

    /**
     * 生成签名. MD5加密
     * 按照字段名降序排列
     * @param data     待签名数据
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data){
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        ArrayList<String> list = new ArrayList<>();
        for (String k : keyArray) {
            if (k.equals("sign")) {
                continue;
            }
            // 参数值为空，则不参与签名
            if (StringUtils.isNotEmpty(data.get(k))) {
                list.add(k);
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
            }
        }
        System.out.println("list = " + JSONObject.toJSONString(list));
        String a = "1";
        return MD5(sb.toString()).toUpperCase();
        }

    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    private static String MD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            log.error("MD5加密失败!");
            return null;
        }
    }

}
