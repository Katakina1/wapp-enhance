package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedNoGeneratePdfModel implements ValueEnum<Integer>{

    Merge_All(0, "全部合并"),

    Split_By_Seller(1, "按销方公司拆分"),

    Split_By_Purchaser(2,"按购方公司拆分");

    private final Integer value;

    private final String description;

}
