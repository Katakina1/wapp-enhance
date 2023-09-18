package com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: ChenHang
 * @Date: 2023/6/27 16:22
 */
@Data
public class BMSResultDTO<T> implements Serializable {

    private static final String OK = "0";
    private static final String CREATED = "201";
    private static final String UNAUTHORIZED = "401";
    private static final String FORBIDDEN = "403";
    private static final String NOT_FOUND = "404";

    /**
     * 调用方应用名称
     */
    private String appName;
    /**
     * 响应代码
     */
    private String code;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 响应时间(时间戳)
     */
    private Long responseTime;
    /**
     * 拓展响应代码
     */
    private String returnCode;
    /**
     * 请求跟踪
     */
    private String traceId;
    /**
     * 返回结果
     */
    private List<T> result;

    @JsonIgnore
    public boolean isSuccess() {
        if (this != null && OK.equals(this.code)) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    public boolean isFail() {
        return !isSuccess();
    }

}
