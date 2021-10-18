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
    @JsonPropertyDescription(value = "发票代码")
    private String invoiceCode;

    @JsonProperty(value = "invoiceNo")
    @JsonPropertyDescription(value = "发票号码")
    private String invoiceNo;

    @JsonProperty(value = "paperDrewDate")
    @JsonPropertyDescription(value = "开票日期")
    private String invoiceDate;

    @JsonProperty(value = "purchaserName")
    @JsonPropertyDescription(value = "购方名称")
    private String gfName;

    @JsonProperty(value = "purchaserTaxNo")
    @JsonPropertyDescription(value = "购方税号")
    private String gfTaxNo;

    @JsonProperty(value = "purchaserAddrTel")
    @JsonPropertyDescription(value = "购方地址电话")
    private String gfAddressAndPhone;

    @JsonProperty(value = "purchaserBankInfo")
    @JsonPropertyDescription(value = "购方开户行及账号")
    private String gfBankAndNo;

    @JsonProperty(value = "sellerName")
    @JsonPropertyDescription(value = "销方名称")
    private String xfName;

    @JsonProperty(value = "sellerTaxNo")
    @JsonPropertyDescription(value = "销方税号")
    private String xfTaxNo;

    @JsonProperty(value = "sellerAddrTel")
    @JsonPropertyDescription(value = "销方地址及电话")
    private String xfAddressAndPhone;

    @JsonProperty(value = "sellerBankInfo")
    @JsonPropertyDescription(value = "销方开户行及账号")
    private String xfBankAndNo;

    @JsonProperty(value = "amountWithoutTax")
    @JsonPropertyDescription(value = "不含税金额")
    private String invoiceAmount;

    @JsonProperty(value = "taxAmount")
    @JsonPropertyDescription(value = "税额")
    private String taxAmount;

    @JsonProperty(value = "amountWithTax")
    @JsonPropertyDescription(value = "含税金额（价税合计）")
    private String totalAmount;

    @JsonProperty(value = "checkCode")
    @JsonPropertyDescription(value = "校验码")
    private String checkCode;

    @JsonProperty(value = "remark")
    @JsonPropertyDescription(value = "备注")
    private String remark;

    @JsonProperty(value = "cashierName")
    @JsonPropertyDescription(value = "收款人")
    private String cashierName;

    @JsonProperty(value = "checkerName")
    @JsonPropertyDescription(value = "复核人")
    private String checkerName;

    @JsonProperty(value = "invoicerName")
    @JsonPropertyDescription(value = "开票人")
    private String invoicerName;

    /**
     * invoice_status  发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常 5-蓝冲
     */
    @JsonProperty(value = "status")
    @JsonPropertyDescription(value = "发票状态:0作废,1正常,2红冲,-1失控,-2异常,-9未知")
    private String status;

    @JsonProperty(value = "specialType")
    @JsonPropertyDescription(value = "发票特殊类型标识: 18-成品油 06-通行费")
    private String specialType;

    @JsonProperty(value = "isSaleList")
    @JsonPropertyDescription(value = "发是否有销货清单: 0-无1-有")
    private String isSaleList;

    @JsonProperty(value = "machineCode")
    @JsonPropertyDescription(value = "机器编码")
    private String machinecode;
}
