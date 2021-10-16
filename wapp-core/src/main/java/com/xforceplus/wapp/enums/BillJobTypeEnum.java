package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * @program: wapp-generator
 * @description: 单据任务类型
 * @author: Kenny Wong
 * @create: 2021-10-14 14:20
 **/
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
    private final int jobType;

    BillJobTypeEnum(int jobType) {
        this.jobType = jobType;
    }
}
