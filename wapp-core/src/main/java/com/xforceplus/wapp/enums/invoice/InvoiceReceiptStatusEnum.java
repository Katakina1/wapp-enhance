package com.xforceplus.wapp.enums.invoice;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 签收状态
 * @date : 2022/09/19 13:39
 **/
@AllArgsConstructor
@Getter
public enum InvoiceReceiptStatusEnum {

    /**
     * 签收状态 1-已签收 0-未签收
     */
    YES("1", "已签收"),
    NO("0", "未签收");

    private String code;
    private String desc;
}
