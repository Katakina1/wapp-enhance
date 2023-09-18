package com.xforceplus.wapp.enums;


import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum TXfSysLogModuleEnum implements ValueEnum<String> {

    MATCH_INVOICE_DETAIL("MATCH_INVOICE_DETAIL", "蓝票明细匹配"),
    CLAIM_MATCH_ITEM("CLAIM_MATCH_ITEM", "索赔单匹配明细"),
    CLAIM_TO_SETTLEMENT("CLAIM_TO_SETTLEMENT", "索赔单合并结算单"),
    AGREEMENT_TO_SETTLEMENT("AGREEMENT_TO_SETTLEMENT", "协议单合并结算单"),
    PRE_INVOICE_RED_NO("PRE_INVOICE_RED_NO", "预制发票红字事件");


    private final String value;
    private final String des;
}
