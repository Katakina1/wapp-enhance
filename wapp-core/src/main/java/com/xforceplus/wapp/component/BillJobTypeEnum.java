package com.xforceplus.wapp.component;

import lombok.Getter;

public enum BillJobTypeEnum {

    /**
     * 索赔单任务
     */
    CLAIM_BILL_JOB(1),
    /**
     * 协议单任务
     */
    AGREEMENT_BILL_JOB(2),
    /**
     * EPD单任务
     */
    EPD_BILL_JOB(3);

    @Getter
    private int jobType;

    BillJobTypeEnum(int jobType) {
        this.jobType = jobType;
    }
}
