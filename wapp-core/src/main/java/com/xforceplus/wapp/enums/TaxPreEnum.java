package com.xforceplus.wapp.enums;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * 是否享受税收优惠政策0-不1-享受
 */
public enum TaxPreEnum {
    EXPORT_TAX("0", "不享受"),
    EXEMPT_TAX("1", "享受"),
    STRING_EMPTY("", "空"),
    NULL_EMPTY(null, "空")
    ;

    @Getter
    private final String code;
    @Getter
    private final String description;

    TaxPreEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }


    public static TaxPreEnum fromCode(String code) {
        return Stream.of(values())
                .filter(t -> t.code.equals(code))
                .findFirst().orElse(null);
    }
}
