package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 红字信息申请状态
 */
@Getter
@AllArgsConstructor
public enum RedNoApplyingStatus implements ValueEnum<Integer>{
    WAIT_TO_APPLY(1,"未申请"),
    APPLYING(2,"申请中"),
    APPLIED(3,"已申请"),
    WAIT_TO_APPROVE(4,"撤销待审核"),
    HANG_APPLY(5,"申请挂起"),
    ;

    private final Integer value;
    private final String desc;

    public static RedNoApplyingStatus fromValue(int value) {
        return Arrays.stream(values()).filter(s -> s.getValue() == value)
                .findAny().orElse(null);
    }

}
