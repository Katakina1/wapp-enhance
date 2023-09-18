package com.xforceplus.wapp.enums;

import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

/**
 * 业务单状态
 */
public enum TXfDeductStatusEnum {

    CLAIM_NO_MATCH_ITEM(101, "索赔单:待匹配明细"),
    CLAIM_NO_MATCH_TAX_NO(102, "索赔单:待确认税编"),
    CLAIM_NO_MATCH_TAX_DIFF(103, "索赔单:待确认税差"),
    CLAIM_NO_MATCH_BLUE_INVOICE(104, "索赔单:待匹配蓝票"),
    CLAIM_NO_MATCH_SETTLEMENT(105, "索赔单:待生成结算单"),
    CLAIM_MATCH_SETTLEMENT(106, "索赔单:已生成结算单"),
    CLAIM_WAIT_CHECK(107, "索赔单:待审核"),
    CLAIM_DESTROY(108, "索赔单:已作废"),

    // 初始状态 201，合并后 进入 205，匹配完蓝票后 进入 202
    AGREEMENT_NO_MATCH_SETTLEMENT(201, "协议单:待匹配结算单"),
    AGREEMENT_MATCH_SETTLEMENT(202, "协议单:已匹配结算单"),
    AGREEMENT_NO_MATCH_BLUE_INVOICE(205, "协议单:待匹配蓝票"),
    AGREEMENT_DESTROY(206, "协议单:已作废"),

    EPD_NO_MATCH_SETTLEMENT(301, "EPD单:待匹配结算单"),
    EPD_MATCH_SETTLEMENT(302, "EPD单:已匹配结算单"),
    EPD_NO_MATCH_BLUE_INVOICE(303, "EPD单:待匹配蓝票"),
    EPD_DESTROY(304, "EPD单:已作废"),

    LOCK(1, "锁定"),
    UNLOCK(0, "解锁"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    TXfDeductStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TXfDeductStatusEnum getEnumByCode(Integer code) {
        return Stream.of(TXfDeductStatusEnum.values())
                .filter(t -> t.getCode().equals(code)).findFirst().orElseGet( null);
    }
}
