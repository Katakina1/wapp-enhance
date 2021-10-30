package com.xforceplus.wapp.common.exception;

/**
 * @program: wapp-generator
 * @description: 单据匹配蓝票子流程中，无可用蓝票将抛出此异常
 * @author: Kenny Wong
 * @create: 2021-10-20 16:26
 **/
public class NoSuchInvoiceException extends EnhanceRuntimeException {

    public NoSuchInvoiceException() {
        super("没有匹配到合适的蓝票，请稍后重试");
    }

    public NoSuchInvoiceException(String message) {
        super(message);
    }

    public NoSuchInvoiceException(String message, String code) {
        super(message, code);
    }

}
