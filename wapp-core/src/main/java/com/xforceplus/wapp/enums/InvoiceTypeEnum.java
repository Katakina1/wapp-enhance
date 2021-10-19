package com.xforceplus.wapp.enums;

import com.google.common.collect.Maps;
import com.xforceplus.wapp.common.enums.ValueEnum;
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
public enum InvoiceTypeEnum implements ValueEnum<String> {

    /**
     * 专票
     */
    SPECIAL_INVOICE("01", "增值税专用发票"),

    /**
     * 机票
     */
    MOTOR_INVOICE("03", "机动车销售统一发票"),

    GENERAL_INVOICE("04", "增值税普通发票"),

    E_SPECIAL_INVOICE("08", "增值税电子专用发票"),

    E_INVOICE("10", "增值税电子普通发票"),

    VOLUME_INVOICE("11", "增值税普通发票（卷票）"),

    TOLLS_INVOICE("14", "增值税电子普通发票（通行费）");

    private final String value;
    private final String resultTip;

    public static Map<String, String> invoiceTypeMap() {
        final Map<String, String> map = Maps.newHashMapWithExpectedSize(6);
        map.put(SPECIAL_INVOICE.getValue(), SPECIAL_INVOICE.getResultTip());
        map.put(MOTOR_INVOICE.getValue(), MOTOR_INVOICE.getResultTip());
        map.put(GENERAL_INVOICE.getValue(), GENERAL_INVOICE.getResultTip());
        map.put(E_INVOICE.getValue(), E_INVOICE.getResultTip());
        map.put(VOLUME_INVOICE.getValue(), VOLUME_INVOICE.getResultTip());
        map.put(TOLLS_INVOICE.getValue(), TOLLS_INVOICE.getResultTip());
        return map;
    }

    public static boolean isElectronic(String invoiceType) {
        return E_INVOICE.getValue().equals(invoiceType) || E_SPECIAL_INVOICE.getValue().equals(invoiceType);
    }

}
