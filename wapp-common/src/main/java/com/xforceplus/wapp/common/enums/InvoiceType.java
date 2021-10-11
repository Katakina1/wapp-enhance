package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvoiceType implements ValueEnum<String>{

    NORMAL("c", "增值税普通发票"),
    SPECIAL("s","增值税专用发票"),
    SPECIAL_ELECTRONIC("se","增值税电子专用发票"),
    VEHICLE("v", "机动车销售发票"),
    UNIVERSAL("t", "通用机打发票"),
    ELECTRONIC("ce", "增值税电子普通发票"),
    NORMAL_ROLL("ju"," 增值税普通发票(卷票)"),
    ELEC_BLOCKCHAIN("ceb", "电子普通发票（区块链）");


    private final String value;
    private final String description;

}
