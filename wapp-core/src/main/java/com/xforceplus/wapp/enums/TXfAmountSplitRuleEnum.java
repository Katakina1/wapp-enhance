package com.xforceplus.wapp.enums;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;

/**
 *  拆票规则枚举
 */
public enum TXfAmountSplitRuleEnum implements ValueEnum<Integer> {
      SplitNon(0,"保持明细现状"),//协议结算单确认，无需客户弹框确认
      SplitQuantity(1,"按单价拆数量"),
      SplitPrice(3,"按数量拆单价"),

      SplitPriceAndQuantity(4,"数量和单价为空"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    TXfAmountSplitRuleEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
