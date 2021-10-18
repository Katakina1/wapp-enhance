package com.xforceplus.wapp.enums;


import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ZZW
 */

@Getter
@AllArgsConstructor
public enum XFDeductionBusinessTypeEnum implements ValueEnum<Integer> {
    /**
     *
     */
    CLAIM_BILL(1, "索赔单"),
    AGREEMENT_BILL(2, "协议单"),
    EPD_BILL(3, "EPD单"),
    ;
    private final Integer value;
    private final String des;
}
