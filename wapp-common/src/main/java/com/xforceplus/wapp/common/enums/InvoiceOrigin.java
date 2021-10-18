package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * invoiceOrigin
 * 红字信息来源1.索赔单，2协议单，3.EPD ,4 导入
 * 5.撤销待审批  单独页面审批
 * approve_status
 */
@Getter
@AllArgsConstructor
public enum InvoiceOrigin implements ValueEnum<Integer>{
    CLAIM(1,"索赔单"),
    AGREE(2,"协议单"),
    EPD(3,"EPD单"),
    IMPORT(4,"导入");

    private final Integer value;
    private final String desc;

}
