package com.xforceplus.wapp.enums;

import lombok.Getter;

import java.util.stream.Stream;

public enum ZeroTaxEnum {

    EXPORT_TAX("0", "出口退税"),
    EXEMPT_TAX("1", "免税"),
    NO_TAX("2", "不征税"),
    NORMAL_TAX("3", "普通0税率"),
    STRING_EMPTY("", "非零税率"),
    NULL_EMPTY(null, "非零税率");

    @Getter
    private final String code;
    @Getter
    private final String description;

    ZeroTaxEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }


    public static ZeroTaxEnum fromCode(String code) {
        return Stream.of(values())
                .filter(t -> t.code.equals(code))
                .findFirst().orElse(null);
    }
}
