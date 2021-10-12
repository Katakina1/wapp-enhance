package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 预制发票状态
 */
public enum XfPreInvoiceEnum {

    NO_APPLY_RED_NOTIFICATION(1,"待申请红字信息"),
    NO_UPLOAD_RED_INVOOCE(2,"待上传红票"),
    UPLOAD_RED_INVOOCE(3,"已上传上传红票"),
    WAIT_CHECK(4,"待审核"),
    CANCEL(5,"已撤销"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    XfPreInvoiceEnum(Integer code,String desc){
        this.code = code;
        this.desc = desc;
    }
}
