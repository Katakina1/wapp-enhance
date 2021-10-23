package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedNoApplyType implements ValueEnum<Integer>{

    PURCHASER_APPLY_WITH_DEDUCTION(0,"购方申请—已抵扣"),

    PURCHASER_APPLY_WITHOUT_DEDUCTION(1,"购方申请—未抵扣"),

    SELLER_APPLY(2,"销方申请");


    private final Integer value;
    private final String description;

}
