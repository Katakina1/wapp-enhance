package com.xforceplus.wapp.enums.invoice;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 发票认证状态
 * @date : 2022/09/15 17:17
 **/
public enum  InvoiceAuthStatusEnum {
    /**
     * 证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
     */
    NO_AUTH("0", "未认证"),
    SELECT_NO_CONFIRM("1", "已勾选未确认"),
    SELECT_CONFIRM("2", "已确认"),
    SEND_AUTH("3", "已发送认证"),
    SUCCESS_AUTH("4", "认证成功"),
    FAIL_AUTH("5", "认证失败");

    private String code;

    private String desc;

    InvoiceAuthStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String code() {
        return this.code;
    }

    public String desc() {
        return this.desc;
    }
}
