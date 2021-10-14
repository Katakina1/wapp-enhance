package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 红字信息申请状态
 */
@Getter
@AllArgsConstructor
public enum RedNoApplyingStatus implements ValueEnum<Integer>{
    WAIT_TO_APPLY(1,"未申请"),
    APPLYING(2,"申请中"),
    APPLIED(3,"已申请"),
    WAIT_TO_APPROVE(4,"撤销待审核");

    private final Integer value;
    private final String desc;

}
