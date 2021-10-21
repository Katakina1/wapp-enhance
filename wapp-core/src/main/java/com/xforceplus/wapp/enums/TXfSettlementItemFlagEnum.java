package com.xforceplus.wapp.enums;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;

/**
 *  结算单明细状态 0 正常 1 待匹配税编 2 待确认金额
 */
public enum TXfSettlementItemFlagEnum implements ValueEnum<Integer> {

    NORMAL(0,"正常"),
    WAIT_MATCH_TAX_CODE(1,"待匹配税编"),
    WAIT_MATCH_CONFIRM_AMOUNT(2,"待确认金额"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    TXfSettlementItemFlagEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
