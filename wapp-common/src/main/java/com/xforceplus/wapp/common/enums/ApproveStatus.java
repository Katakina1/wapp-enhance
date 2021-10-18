package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 红字信息审批状态  审批状态 1. 审核通过,2. 审核不通过,3. 已核销,4. 已撤销,
 * 5.撤销待审批  单独页面审批
 * approve_status
 */
@Getter
@AllArgsConstructor
public enum ApproveStatus implements ValueEnum<Integer>{
    OTHERS(0,"其他状态"),
    APPROVE_PASS(1,"审核通过"),
    APPROVE_FAIL(2,"审核不通过"),
    ALREADY_USE(3,"已核销"),
    ALREADY_ROLL_BACK(4,"已撤销"),
    WAIT_TO_APPROVE(5,"撤销待审批");



    private final Integer value;
    private final String desc;

}
