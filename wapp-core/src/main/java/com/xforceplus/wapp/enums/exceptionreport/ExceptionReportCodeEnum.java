package com.xforceplus.wapp.enums.exceptionreport;

import lombok.Getter;

/**
 * 例外报告代码枚举
 */
public enum ExceptionReportCodeEnum {
    /**
     * 未匹配到蓝票
     */
    NOT_MATCH_BLUE_INVOICE("X001","匹配不到蓝票"),
    WITH_DIFF_TAX("S001","税差"),
    NOT_FOUND_BLUE_TAX_RATE("S002","找不到对应蓝票税率"),
    NOT_MATCH_GOODS_TAX("S003","匹配不到商品税编"),
    NOT_MATCH_CLAIM_DETAIL("S004","无索赔明细");
    @Getter
    private String code;
    @Getter
    private String description;

    ExceptionReportCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
