package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 预制发票状态
 */
public enum XfPreInvoiceStatusEnum {

    NO_APPLY_RED_NOTIFICATION(1,"待申请红字信息"),
    NO_UPLOAD_RED_INVOICE(2,"待开红票"),
    UPLOAD_RED_INVOICE(3,"已开红票"),
    WAIT_CHECK(4,"待审核"),
    CANCEL(5,"已撤销"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    XfPreInvoiceStatusEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
