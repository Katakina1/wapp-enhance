package com.xforceplus.wapp.enums;

import lombok.Getter;

public enum TXfDeductInvoiceBusinessTypeEnum {

    CLAIM_BILL(1,"索赔单") ,
    SETTLEMENT(2,"结算单");
    @Getter
    private Integer type;
    @Getter
    private String des;

    TXfDeductInvoiceBusinessTypeEnum(Integer type, String des) {
        this.type = type;
        this.des = des;
    }
}
