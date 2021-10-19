package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 业务单状态
 * 索赔单:101待匹配明细;102待确认税编;103待确认税差;104待匹配蓝票;105:待匹配结算单;106已匹配结算单;107待审核;108已撤销
 * 协议单:201待匹配结算单;202已匹配结算单;203已锁定;204已取消
 * EPD单:301待匹配结算单;302已匹配结算单
 */
public enum TXfBillDeductStatusEnum {

    CLAIM_NO_MATCH_ITEM(101, "索赔单:待匹配明细"),
    CLAIM_NO_MATCH_TAX_NO(102, "索赔单:待确认税编"),
    CLAIM_NO_MATCH_TAX_DIFF(103, "索赔单:待确认税差"),
    CLAIM_NO_MATCH_BLUE_INVOICE(104, "索赔单:待匹配蓝票"),
    CLAIM_NO_MATCH_SETTLEMENT(105, "索赔单:待生成结算单"),
    CLAIM_MATCH_SETTLEMENT(106, "索赔单:已生成结算单"),
    CLAIM_WAIT_CHECK(107, "索赔单:待审核"),
    CLAIM_DESTROY(108, "索赔单:已作废"),

    AGREEMENT_NO_MATCH_SETTLEMENT(201, "协议单:待匹配结算单"),
    AGREEMENT_MATCH_SETTLEMENT(202, "协议单:已匹配结算单"),

    AGREEMENT_NO_MATCH_BLUE_INVOICE(205, "协议单:待匹配蓝票"),
    EPD_NO_MATCH_SETTLEMENT(301, "EPD单:待匹配结算单"),
    EPD_MATCH_SETTLEMENT(302, "EPD单:已匹配结算单"),
    EPD_NO_MATCH_BLUE_INVOICE(303, "EPD单:待匹配蓝票"),
    LOCK(1, " 已锁定"),
    UNLOCK(0, "解锁"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    TXfBillDeductStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
