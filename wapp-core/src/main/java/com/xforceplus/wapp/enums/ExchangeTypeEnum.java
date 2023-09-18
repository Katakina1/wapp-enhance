package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 换票状态枚举值
 */
public enum ExchangeTypeEnum {
    EXCHANGE_TICKEY_TYPE_PAPER("0", "纸票"),
    EXCHANGE_TICKEY_TYPE_EL("1", "电票");
    @Getter
    private final String code;
    @Getter
    private final String description;


    ExchangeTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getValue(String code) {
        for (ExchangeTypeEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getDescription();
            }
        }
        return null;
    }


}
