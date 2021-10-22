package com.xforceplus.wapp.enums;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;

/**
 * 结算单状态
 */
public enum TXfInvoiceDeductStatusEnum implements ValueEnum<Integer> {

    NORMAL(0,"正常"),
    DELETE(1,"删除"),

    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    TXfInvoiceDeductStatusEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
