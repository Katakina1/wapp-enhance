package com.xforceplus.wapp.enums.exceptionreport;

import lombok.Getter;

/**
 * 例外报告类型，1 索赔单，2 协议单 ，3 EPD
 */
public enum ExceptionReportTypeEnum {
    /**
     * 索赔单
     */
    CLAIM(1),
    /**
     * EPD 单
     */
    EPD(3),
    /**
     * 协议单
     */
    AGREEMENT(2);
    /**
     * 类型
     */
    @Getter
    private int type;
    ExceptionReportTypeEnum(int type){
        this.type=type;
    }
}
