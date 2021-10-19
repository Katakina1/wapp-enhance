package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 *  拆票规则枚举
 */
public enum TXfAmountSplitRuleEnum {
      SplitQuantity(1,"按单价拆数量"),
      SplitPrice(3,"按数量拆单价"),

    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    TXfAmountSplitRuleEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
