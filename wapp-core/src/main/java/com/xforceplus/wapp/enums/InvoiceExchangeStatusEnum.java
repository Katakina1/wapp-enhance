package com.xforceplus.wapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by SunShiyong on 2021/11/19.
 */
@AllArgsConstructor
@Getter
public enum InvoiceExchangeStatusEnum {

    TO_BE_EXCHANGE(0,"待换票"),
    UPLOADED(1,"已上传"),
    FINISHED(2,"已完成"),
    DELETE(9,"删除");


    private int code;
    private String desc;
}
