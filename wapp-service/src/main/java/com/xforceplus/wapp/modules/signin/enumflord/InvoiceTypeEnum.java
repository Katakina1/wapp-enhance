package com.xforceplus.wapp.modules.signin.enumflord;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * 发票类型
 *
 * @author Colin.hu
 * @date 4/26/2018
 */
@AllArgsConstructor
@Getter
public enum InvoiceTypeEnum {

    /**
     * 专票
     */
    SPECIAL_INVOICE("01", "增值税专用发票"),

    /**
     * 机票
     */
    MOTOR_INVOICE("03", "机动车销售统一发票"),

    GENERAL_INVOICE("04", "增值税普通发票"),

    E_INVOICE("10", "增值税电子普通发票"),

    VOLUME_INVOICE("11", "增值税普通发票（卷票）"),

    TOLLS_INVOICE("14", "增值税电子普通发票（通行费）");

    private String resultCode;

    private String resultTip;

    public static Map<String, String> invoiceTypeMap() {
        final Map<String, String> map = Maps.newHashMapWithExpectedSize(6);
        map.put(SPECIAL_INVOICE.getResultCode(), SPECIAL_INVOICE.getResultTip());
        map.put(MOTOR_INVOICE.getResultCode(), MOTOR_INVOICE.getResultTip());
        map.put(GENERAL_INVOICE.getResultCode(), GENERAL_INVOICE.getResultTip());
        map.put(E_INVOICE.getResultCode(), E_INVOICE.getResultTip());
        map.put(VOLUME_INVOICE.getResultCode(), VOLUME_INVOICE.getResultTip());
        map.put(TOLLS_INVOICE.getResultCode(), TOLLS_INVOICE.getResultTip());
        return map;
    }
}
