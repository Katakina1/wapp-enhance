package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 业务单状态
 * 索赔单:101待匹配明细;102待确认税编;103待确认税差;104待匹配蓝票;105:待匹配结算单;106已匹配结算单;107待审核;108已撤销
 * 协议单:201待匹配结算单;202已匹配结算单;203已锁定;204已取消
 * EPD单:301待匹配结算单;302已匹配结算单
 */
public enum TXfBillDeductStatusEnum {

    NO_MATCH_ITEM(101, " 待匹配明细"),
    NO_MATCH_TAX_NO(102, " 待确认税编"),
    NO_MATCH_TAX_DIFF(103, " 待确认税差"),
    NO_MATCH_BLUE_INVOICE(104, " 待匹配蓝票"),
    NO_MATCH_SETTLEMENT(105, " 待生成结算单"),
    MATCH_SETTLEMENT(106, " 已生成结算单"),
    WAIT_CHECK(107, " 待审核"),
    DESTROY(108, " 已作废"),
    LOCK(109, " 已锁定"),
    UNLOCK(110, "协议单:已取消"),
//
//    AGREEMENT_NO_MATCH_SETTLEMENT(201, "协议单:待匹配结算单"),
//    AGREEMENT_MATCH_SETTLEMENT(202, "协议单:已匹配结算单"),
//    AGREEMENT_LOCK(203, "协议单:已锁定"),
//    AGREEMENT_UNLOCK(204, "协议单:已取消"),
//    AGREEMENT_NO_MATCH_BLUE_INVOICE(205, "协议单:待匹配蓝票"),
//
//
//    EPD_NO_MATCH_SETTLEMENT(301, "EPD单:待匹配结算单"),
//    EPD_MATCH_SETTLEMENT(302, "EPD单:已匹配结算单"),
//    EPD_NO_MATCH_BLUE_INVOICE(303, "EPD单:待匹配蓝票"),

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
