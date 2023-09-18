package com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.send;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 请求BMS接口入参实体
 * @Author: ChenHang
 * @Date: 2023/6/27 15:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendBMSDTO<T> implements Serializable {

    /**
     * 调用方应用名称
     */
    private String appName;

    /**
     * param的格式. json 或者 string，对象是json,基本类型是String
     */
    private String format;

    private T param;

    /**
     * 签名
     * 必须使用fastjson,然后字符串拼接如下
     * appName + source + timestamp + format + version
     * + ("json".equals(format) ? JSONObject.toJSONString(param) : param.toString());
     * 将拼接的字段进行resultStr.append(appSecret).append(param).append(appSecret);
     */
    private String sign;

    private String source;

    private String timestamp;

    private String version;

}
