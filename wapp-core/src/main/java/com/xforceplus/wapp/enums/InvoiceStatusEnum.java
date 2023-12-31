package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 大象慧云发票状态枚举
 */
public enum InvoiceStatusEnum {
    INVOICE_STATUS_NORMAL("0", "正常"),
    INVOICE_STATUS_LOSE("1", "失控"),
    INVOICE_STATUS_DELETE("2", "作废"),
    INVOICE_STATUS_SEND_RED("3", "红冲"),
    ANTU_STATUS_EXCEPTION("4", "异常"),
    INVOICE_STATUS_SEND_BLUE("5","蓝冲");

    @Getter
    private final String code;
    @Getter
    private final String description;


    InvoiceStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getValue(String code) {
        for (InvoiceStatusEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getDescription();
            }
        }
        return null;
    }

    public static String getCode(String val) {
        for (InvoiceStatusEnum ele : values()) {
            if (ele.getDescription().equals(val)) {
                return ele.getCode();
            }
        }
        return null;
    }


}
