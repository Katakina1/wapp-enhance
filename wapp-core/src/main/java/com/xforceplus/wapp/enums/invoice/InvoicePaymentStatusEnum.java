package com.xforceplus.wapp.enums.invoice;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 付款状态
 * @date : 2022/09/19 13:42
 **/
@AllArgsConstructor
@Getter
public enum InvoicePaymentStatusEnum {

    /**
     * 付款状态 1-已付款 0-未付款
     */
    YES("1", "已付款"),
    NO("0", "未付款");

    private String code;
    private String desc;
}
