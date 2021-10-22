package com.xforceplus.wapp.enums;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;

/**
 * 结算单状态
 */
public enum TXfInvoiceDeductTypeEnum implements ValueEnum<Integer> {

    CLAIM(1,"索赔"),
    SETTLEMENT(2,"结算单"),

    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    TXfInvoiceDeductTypeEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
