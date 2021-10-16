package com.xforceplus.wapp.modules.rednotification.exception;

import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;

/**
 * 自定义异常
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年10月27日 下午10:11:27
 */
public class RRException extends EnhanceRuntimeException {
    private static final long serialVersionUID = 1L;


    public RRException(String msg) {
        super(msg);
    }

    public RRException(String msg, Throwable e) {
        super(msg, e);
    }

    public RRException(String msg, String code) {
        super(msg,code);
    }

    public RRException(String msg, String code, Throwable e) {
        super(msg, e,code);
    }



}
