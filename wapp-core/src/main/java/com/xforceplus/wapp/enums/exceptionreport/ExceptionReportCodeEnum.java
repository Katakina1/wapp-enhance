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
    NOT_MATCH_BLUE_INVOICE("X001","匹配不到蓝票", 1, "1401"),
    WITH_DIFF_TAX("S001","税差", 0, ""),
    NOT_FOUND_BLUE_TAX_RATE("S002","找不到对应税率的蓝票", 0, "1205"),
    NOT_MATCH_GOODS_TAX("S003","匹配不到商品税编", 0, "1203"),
    NOT_MATCH_CLAIM_DETAIL("S004","无索赔明细", 0, "1201"),
    CLAIM_DETAIL_ZERO_TAX_RATE("S005","0税率明细税差", 0, ""),
    PART_MATCH_CLAIM_DETAIL("S006","只有部分索赔明细", 0, "1201"),
	VENDOR_NO_FAIL("S007","供应商编号错误", 0, "1201");
	
    @Getter
    private final String code;
    @Getter
    private final String description;

    private final Integer type;
	/**
	 * 对应操作code  OperateLogEnum
	 */
	@Getter
    private final String operateKind;

	ExceptionReportCodeEnum(String code, String description, Integer type, String operateKind) {
		this.code = code;
		this.description = description;
		this.type = type;
		this.operateKind = operateKind;
	}


	public static List<ExceptionReportCodeEnum> getClaimCodes() {
		final ExceptionReportCodeEnum[] values = ExceptionReportCodeEnum.values();
		return Arrays.stream(values).filter(x -> Objects.equals(x.type, 0)).collect(Collectors.toList());
	}

	public static List<ExceptionReportCodeEnum> getAgreementOrEpdCodes() {
		final ExceptionReportCodeEnum[] values = ExceptionReportCodeEnum.values();
		return Arrays.stream(values).filter(x -> Objects.equals(x.type, 1)).collect(Collectors.toList());
	}

	public static ExceptionReportCodeEnum fromCode(String code) {
		return Arrays.stream(ExceptionReportCodeEnum.values()).filter(codeEnum -> codeEnum.getCode().equals(code)).findFirst().orElse(null);
	}

}
