package com.xforceplus.wapp.enums;


import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ZZW
 */

@Getter
@AllArgsConstructor
public enum TXfDeductionBusinessTypeEnum implements ValueEnum<Integer> {
    /**
     *
     */
    CLAIM_BILL(1, "索赔单","SP"),
    AGREEMENT_BILL(2, "协议单","XY"),
    EPD_BILL(3, "EPD单","EPD"),
    ;
    private final Integer value;
    private final String des;
    private final String prefix;
}
