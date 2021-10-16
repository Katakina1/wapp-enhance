package com.xforceplus.wapp.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

/**
 * @author masp mashaopeng@xforceplus.com
 */
@Data
public class TaxWareInvoice {
    @JsonProperty(value = "invoiceType")
    @JsonPropertyDescription(value = "发票类型：增值税专用发票/增值普通发票/增值税电子普通发票")
    private String invoiceType;

    @JsonProperty(value = "invoiceCode")
    @JsonPropertyDescription(value = "税收分类编码")
    private String invoiceCode;

    @JsonProperty(value = "invoiceNo")
    @JsonPropertyDescription(value = "税收分类编码")
    private String invoiceNo;

    @JsonProperty(value = "paperDrewDate")
    @JsonPropertyDescription(value = "税收分类编码")
    private String paperDrewDate;

    @JsonProperty(value = "purchaserName")
    @JsonPropertyDescription(value = "税收分类编码")
    private String purchaserName;

    @JsonProperty(value = "purchaserTaxNo")
    @JsonPropertyDescription(value = "税收分类编码")
    private String purchaserTaxNo;

    @JsonProperty(value = "purchaserAddrTel")
    @JsonPropertyDescription(value = "税收分类编码")
    private String purchaserAddrTel;

    @JsonProperty(value = "purchaserBankInfo")
    @JsonPropertyDescription(value = "税收分类编码")
    private String purchaserBankInfo;

    @JsonProperty(value = "sellerName")
    @JsonPropertyDescription(value = "税收分类编码")
    private String sellerName;

    @JsonProperty(value = "sellerTaxNo")
    @JsonPropertyDescription(value = "税收分类编码")
    private String sellerTaxNo;

    @JsonProperty(value = "sellerAddrTel")
    @JsonPropertyDescription(value = "税收分类编码")
    private String sellerAddrTel;

    @JsonProperty(value = "sellerBankInfo")
    @JsonPropertyDescription(value = "税收分类编码")
    private String sellerBankInfo;

    @JsonProperty(value = "amountWithoutTax")
    @JsonPropertyDescription(value = "税收分类编码")
    private String amountWithoutTax;

    @JsonProperty(value = "taxAmount")
    @JsonPropertyDescription(value = "税收分类编码")
    private String taxAmount;

    @JsonProperty(value = "amountWithTax")
    @JsonPropertyDescription(value = "税收分类编码")
    private String amountWithTax;

    @JsonProperty(value = "cipherText")
    @JsonPropertyDescription(value = "税收分类编码")
    private String cipherText;

    @JsonProperty(value = "checkCode")
    @JsonPropertyDescription(value = "税收分类编码")
    private String checkCode;

    @JsonProperty(value = "remark")
    @JsonPropertyDescription(value = "税收分类编码")
    private String remark;

    @JsonProperty(value = "cashierName")
    @JsonPropertyDescription(value = "税收分类编码")
    private String cashierName;

    @JsonProperty(value = "checkerName")
    @JsonPropertyDescription(value = "税收分类编码")
    private String checkerName;

    @JsonProperty(value = "invoicerName")
    @JsonPropertyDescription(value = "税收分类编码")
    private String invoicerName;

    @JsonProperty(value = "status")
    @JsonPropertyDescription(value = "发票状态:0作废,1正常,2红冲,-1失控,-2异常,-9未知")
    private String status;

    @JsonProperty(value = "specialType")
    @JsonPropertyDescription(value = "发票特殊类型标识: 18-成品油 06-通行费")
    private String specialType;

    @JsonProperty(value = "isSaleList")
    @JsonPropertyDescription(value = "税收分类编码")
    private String isSaleList;

    @JsonProperty(value = "machineCode")
    @JsonPropertyDescription(value = "发是否有销货清单 0-无、1-有")
    private String machineCode;

    @JsonProperty(value = "receivingClerk")
    @JsonPropertyDescription(value = "税收分类编码")
    private String receivingClerk;
}
