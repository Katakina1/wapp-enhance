package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PetroleumReason implements ValueEnum<Integer>{

    NUMBER_CHANGE(0, "成品油涉及销售数量变更"),

    AMOUNT_CHANGE(1,"成品油仅涉及销售金额变更");

    private final Integer value;

    private final String description;
}
