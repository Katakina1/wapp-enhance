package com.xforceplus.wapp.enums.exceptionreport;

import lombok.Getter;

/**
 * 例外报告状态 1 未处理，2已处理，3无需处理（显示状态，数据库不存在）
 */
public enum ExceptionReportStatusEnum {
    /**
     * 异常：未处理
     */
    ABNORMAL(1),
    /**
     * 正常：已处理
     */
    NORMAL(2),
    /**
     * 无需处理(显示状态，数据库不存在)
     */
    IGNORE(3);

    /**
     * 类型
     */
    @Getter
    private final Integer type;
    ExceptionReportStatusEnum(int type){
        this.type=type;
    }
}
