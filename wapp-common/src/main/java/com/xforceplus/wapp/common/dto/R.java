package com.xforceplus.wapp.common.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-09 15:09
 **/
@Data
public class R<T> {

    public static final String OK = "XFWAPP0000";
    public static final String FAIL = "XFWAPP0000";

    private T result;
    private String message;
    private String code;

    public R(){}

    public R(T result, String message, String code) {
        this.result = result;
        this.message = message;
        this.code = code;
    }

    public static <T> R<T> ok(T result) {
        R<T> r = new R<>();
        r.code = OK;
        r.result = result;
        return r;
    }

    public static <T> R<T> ok(T result, String message) {
        R<T> r = ok(result);
        r.message = message;
        return r;
    }

    public static  R<?> fail(String message) {
        R<?> r = new R<>();
        r.code = FAIL;
        r.message = message;
        return r;
    }

    public static  R<?> fail(String message, String code) {
        R<?> r = new R<>();
        r.message = message;
        r.code = StringUtils.isBlank(code) ? FAIL : code;
        return r;
    }

}
