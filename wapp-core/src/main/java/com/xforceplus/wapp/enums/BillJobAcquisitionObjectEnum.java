package com.xforceplus.wapp.enums;

import lombok.Getter;

import java.util.Objects;

public enum BillJobAcquisitionObjectEnum {

    /**
     * 单据对象
     */
    BILL_OBJECT(1),
    /**
     * 明细对象或索赔单Hyper明细对象
     */
    BILL_ITEM(2),
    /**
     * 索赔单Sams明细对象
     */
    BILL_ITEM_SAMS(3);

    @Getter
    private final int billObjectCode;

    BillJobAcquisitionObjectEnum(int billObjectCode) {
        this.billObjectCode = billObjectCode;
    }

    public static BillJobAcquisitionObjectEnum fromCode(int billObjectCode) {
        for (BillJobAcquisitionObjectEnum value : BillJobAcquisitionObjectEnum.values()) {
            if (Objects.equals(value.getBillObjectCode(), billObjectCode)) {
                return value;
            }
        }
        return null;
    }
}
