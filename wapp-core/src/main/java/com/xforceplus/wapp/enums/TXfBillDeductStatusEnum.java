package com.xforceplus.wapp.enums;

import lombok.Getter;

public enum TXfBillDeductStatusEnum {

    NO_MATCH_ITEM(1,"待匹配明细"),
    NO_MATCH_TAX_NO(2,"待确认税编"),
    NO_MATCH_TAX_DIFF(3,"待确认税差"),
    NO_MATCH_BLUE_INVOICE(4,"待匹配蓝票"),
    UPLOAD_RED_INVOICE(5,"已匹配"),
    WAIT_CHECK(6,"待审核"),
    CANCEL(7,"已撤销"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    TXfBillDeductStatusEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
