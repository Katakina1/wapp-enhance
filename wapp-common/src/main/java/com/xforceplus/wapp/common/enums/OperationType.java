package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
@Getter
@AllArgsConstructor
public enum OperationType implements ValueEnum<String>{
    CONFIRM("confirm","确认"),
    REJECT("reject","驳回");

    private final String value;
    private final String desc;

}
