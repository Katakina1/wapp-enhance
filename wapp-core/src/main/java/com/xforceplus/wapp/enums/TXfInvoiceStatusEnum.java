package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 预制发票状态
 */
public enum TXfInvoiceStatusEnum {

    NORMAL("0","正常"),
    OUTER_OF_CONTROL("1","失控"),
    CANCEL("2","作废"),
    RED_DASHED("3","红冲"),
    EXCEPTION("4","异常"),
    BLUE_DASHED("5","蓝冲")
    ;

    @Getter
    private String code;
    @Getter
    private String desc;

    TXfInvoiceStatusEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
