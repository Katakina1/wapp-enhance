package com.xforceplus.wapp.enums;

import lombok.Getter;

import java.util.Objects;

public enum BillJobEntryObjectEnum {

    /**
     * 单据对象
     */
    BILL(1),
    /**
     * 明细对象或索赔单Hyper明细对象
     */
    ITEM(2),
    /**
     * 索赔单Sams明细对象
     */
    ITEM_SAMS(3);

    @Getter
    private final int code;

    BillJobEntryObjectEnum(int billObjectCode) {
        this.code = billObjectCode;
    }

    public static BillJobEntryObjectEnum fromCode(int billObjectCode) {
        for (BillJobEntryObjectEnum value : BillJobEntryObjectEnum.values()) {
            if (Objects.equals(value.getCode(), billObjectCode)) {
                return value;
            }
        }
        return null;
    }
}
