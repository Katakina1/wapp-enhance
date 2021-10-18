package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * @program: wapp-generator
 * @description: bill job lock status
 * @author: Kenny Wong
 * @create: 2021-10-14 14:58
 **/
public enum BillJobLockStatusEnum {

    /**
     * 未锁定
     */
    UNLOCKED(0),
    /**
     * 已锁定
     */
    LOCKED(1);

    @Getter
    private final int lockStatus;

    BillJobLockStatusEnum(int lockstatus) {
        this.lockStatus = lockstatus;
    }
}
