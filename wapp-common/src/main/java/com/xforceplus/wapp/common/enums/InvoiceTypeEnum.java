package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 发票类型
 *
 * @author Colin.hu
 * @date 4/26/2018
 */
@AllArgsConstructor
@Getter
public enum InvoiceTypeEnum {

    SPECIAL_INVOICE("s", "01", "增值税专用发票"),

    MOTOR_INVOICE("v", "03", "机动车销售统一发票"),

    GENERAL_INVOICE("c", "04", "增值税普通发票"),

    BLOCK_CHAIN_INVOICE("07", "增值税电子普通发票（区块链）", "cb"),

    ELECTRONIC_INVOICE("se", "08", "增值电子专用发票"),

    E_INVOICE("ce", "10", "增值税电子普通发票"),

    VOLUME_INVOICE("ju", "11", "增值税普通发票（卷票）"),

    TOLLS_INVOICE("", "14", "增值税电子普通发票（通行费）"),

    QC_INVOICE("qc", "16", "全电电子发票（普通发票）"),

    CK_INVOICE("", "17", "海关缴款书"),

    QS_INVOICE("qs", "18", "全电电子发票（增值税专用发票）"),

    SG_INVOICE("", "24", "出口转内销发票"),

    CKSG_INVOICE("", "30", "出口转内销海关缴款书"),

    CKSE_INVOICE("", "80", "出口转内销专用电子发票");

    private String invoiceType;

    private String resultCode;

    private String resultTip;

    public static Optional<String> getResultCode(String invoiceType) {
        return Arrays.stream(InvoiceTypeEnum.values())
                .filter(s -> s.getInvoiceType().equalsIgnoreCase(invoiceType))
                .findFirst()
                .map(InvoiceTypeEnum::getResultCode);
    }

    public static Map<String, InvoiceTypeEnum> invoiceTypeMap() {
        return Arrays.stream(values()).collect(Collectors.toMap(InvoiceTypeEnum::getResultCode, s -> s));
    }

/**

 public static boolean isElectronic(String invoiceType) {
 return E_INVOICE.getValue().equals(invoiceType) || ELECTRONIC_INVOICE.getValue().equals(invoiceType) || BLOCK_CHAIN_INVOICE.getValue().equals(invoiceType)
 || QC_INVOICE.getValue().equals(invoiceType) || QS_INVOICE.getValue().equals(invoiceType);
 }

 */

    /**
     * 专票
     * "01", "08", "18"
     */
    public static EnumSet<InvoiceTypeEnum> specialInvoiceEnums() {
        return EnumSet.of(
                InvoiceTypeEnum.SPECIAL_INVOICE,
                InvoiceTypeEnum.ELECTRONIC_INVOICE,
                InvoiceTypeEnum.QS_INVOICE);
    }

    /**
     * 纸票
     */
    public static EnumSet<InvoiceTypeEnum> paperInvoiceEnums() {
        return EnumSet.of(
                InvoiceTypeEnum.SPECIAL_INVOICE,
                InvoiceTypeEnum.MOTOR_INVOICE,
                InvoiceTypeEnum.GENERAL_INVOICE,
                InvoiceTypeEnum.VOLUME_INVOICE);
    }

    /**
     * 电子发票 发票类型
     * "08", "10", "16", "18"
     */
    public static EnumSet<InvoiceTypeEnum> electronicInvoiceEnums() {
        return EnumSet.of(
                InvoiceTypeEnum.ELECTRONIC_INVOICE,
                InvoiceTypeEnum.E_INVOICE,
                InvoiceTypeEnum.QC_INVOICE,
                InvoiceTypeEnum.QS_INVOICE);
    }

    public static List<String> electronicInvoices() {
        return electronicInvoiceEnums().stream()
                .map(InvoiceTypeEnum::getResultCode).collect(Collectors.toList());
    }

    /**
     * 普通发票 发票类型
     * "04", "10", "11", "14", "16"
     */
    public static EnumSet<InvoiceTypeEnum> generalInvoiceEnums() {
        return EnumSet.of(
                InvoiceTypeEnum.GENERAL_INVOICE,
                InvoiceTypeEnum.E_INVOICE,
                InvoiceTypeEnum.VOLUME_INVOICE,
                InvoiceTypeEnum.TOLLS_INVOICE,
                InvoiceTypeEnum.QC_INVOICE);
    }

    /**
     * 用于BPMS
     * 费用扫描处理->确认
     * @return
     */
    public static EnumSet<InvoiceTypeEnum> bpmsInvoiceEnums() {
        return EnumSet.of(
                InvoiceTypeEnum.SPECIAL_INVOICE,
                InvoiceTypeEnum.GENERAL_INVOICE,
                InvoiceTypeEnum.ELECTRONIC_INVOICE,
                InvoiceTypeEnum.E_INVOICE,
                InvoiceTypeEnum.TOLLS_INVOICE,
                InvoiceTypeEnum.QC_INVOICE,
                InvoiceTypeEnum.QS_INVOICE);
    }

    /**
     * bpms发票类型
     * 用于费用扫描处理->确认
     * @return
     */
    public static List<String> bpmsInvoiceList() {
        return bpmsInvoiceEnums().stream()
                .map(InvoiceTypeEnum::getResultCode).collect(Collectors.toList());
    }

}