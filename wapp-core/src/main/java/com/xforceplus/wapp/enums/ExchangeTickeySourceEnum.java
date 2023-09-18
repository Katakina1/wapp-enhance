package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 换票状态枚举值
 */
public enum ExchangeTickeySourceEnum {
    EXCHANGE_TICKEY_SOURCE_IMPORT("1", "专票下发"),
    EXCHANGE_TICKEY_SOURCE_AUTO("2", "手工导入");
    @Getter
    private final String code;
    @Getter
    private final String description;


    ExchangeTickeySourceEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getValue(String code) {
        for (ExchangeTickeySourceEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getDescription();
            }
        }
        return null;
    }


}
