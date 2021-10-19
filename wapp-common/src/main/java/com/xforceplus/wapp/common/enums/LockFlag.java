package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * invoiceOrigin
 * 锁表示，1正常，2申请锁定中，3撤销锁定中
 * 5.撤销待审批  单独页面审批
 * approve_status
 */
@Getter
@AllArgsConstructor
public enum LockFlag implements ValueEnum<Integer>{
    NORMAL(1,"正常"),
    APPLY_LOCK(2,"申请锁定中"),
    ROLL_BACK_LOCK(3,"撤销锁定中");

    private final Integer value;
    private final String desc;

}
