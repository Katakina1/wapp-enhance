package com.xforceplus.wapp.enums.exceptionreport;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 例外报告代码枚举
 */
public enum ExceptionReportCodeEnum {
    /**
     * 未匹配到蓝票
     */
    NOT_MATCH_BLUE_INVOICE("X001","匹配不到蓝票",1),
    WITH_DIFF_TAX("S001","税差",0),
    NOT_FOUND_BLUE_TAX_RATE("S002","找不到对应蓝票税率",0),
    NOT_MATCH_GOODS_TAX("S003","匹配不到商品税编",0),
    NOT_MATCH_CLAIM_DETAIL("S004","无索赔明细",0);
    @Getter
    private final String code;
    @Getter
    private final String description;

    private final Integer type;

    ExceptionReportCodeEnum(String code, String description,Integer type) {
        this.code = code;
        this.description = description;
        this.type=type;
    }


    public static List<ExceptionReportCodeEnum> getClaimCodes(){
        final ExceptionReportCodeEnum[] values = ExceptionReportCodeEnum.values();
        return Arrays.stream(values).filter(x-> Objects.equals(x.type,0)).collect(Collectors.toList());
    }

    public static List<ExceptionReportCodeEnum> getAgreementOrEpdCodes(){
        final ExceptionReportCodeEnum[] values = ExceptionReportCodeEnum.values();
        return Arrays.stream(values).filter(x-> Objects.equals(x.type,1)).collect(Collectors.toList());
    }



}
