package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpecialInvoiceFlag implements ValueEnum<Integer>{

    NORMAL(0, "常用发票"),

    TRANSPORT(1, "通行费"),

    OIL_PRODUCT(2, "成品油"),

    BLOCK_CHAIN(3, "区块链");

    private final Integer value;
    private final String description;

}
