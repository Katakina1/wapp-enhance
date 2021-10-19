package com.xforceplus.wapp.enums;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mashaopeng@xforceplus.com
 */
@Getter
@AllArgsConstructor
public enum ServiceTypeEnum implements ValueEnum<Integer> {
    /**
     *    1.索赔、2.协议、3.EPD
     */
    CLAIM(1,"索赔"),
    AGREEMENT(2,"协议"),
    EPD(3,"EPD"),
    ;
    private final Integer value;
    private final String message;
}
