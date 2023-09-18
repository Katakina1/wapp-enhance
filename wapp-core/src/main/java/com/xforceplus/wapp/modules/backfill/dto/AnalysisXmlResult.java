package com.xforceplus.wapp.modules.backfill.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@NoArgsConstructor
@Data
public class AnalysisXmlResult {

    @JsonProperty("invoiceDetails")
    private List<InvoiceDetailsDTO> invoiceDetails;
    @JsonProperty("invoiceMain")
    private InvoiceMainDTO invoiceMain;

    @NoArgsConstructor
    @Data
    public static class InvoiceMainDTO {
        @JsonProperty("invoiceType")
        private String invoiceType;
        @JsonProperty("invoiceNo")
        private String invoiceNo;
        @JsonProperty("paperDrewDate")
        private String paperDrewDate;
        @JsonProperty("purchaserName")
        private String purchaserName;
        @JsonProperty("purchaserTaxNo")
        private String purchaserTaxNo;
        @JsonProperty("purchaserAddress")
        private String purchaserAddress;
        @JsonProperty("purchaserTel")
        private String purchaserTel;
        @JsonProperty("purchaserBankName")
        private String purchaserBankName;
        @JsonProperty("purchaserBankAccount")
        private String purchaserBankAccount;
        @JsonProperty("sellerName")
        private String sellerName;
        @JsonProperty("sellerTaxNo")
        private String sellerTaxNo;
        @JsonProperty("sellerAddress")
        private String sellerAddress;
        @JsonProperty("sellerTel")
        private String sellerTel;
        @JsonProperty("sellerBankName")
        private String sellerBankName;
        @JsonProperty("sellerBankAccount")
        private String sellerBankAccount;
        @JsonProperty("amountWithoutTax")
        private String amountWithoutTax;
        @JsonProperty("taxAmount")
        private String taxAmount;
        @JsonProperty("amountWithTax")
        private String amountWithTax;
        @JsonProperty("drawerName")
        private String drawerName;
        @JsonProperty("remark")
        private String remark;
        @JsonProperty("originalInvoiceCode")
        private String originalInvoiceCode;
    }

    @NoArgsConstructor
    @Data
    public static class InvoiceDetailsDTO {
        @JsonProperty("unitPrice")
        private String unitPrice;
        @JsonProperty("amountWithoutTax")
        private String amountWithoutTax;
        @JsonProperty("itemSpec")
        private String itemSpec;
        @JsonProperty("taxRate")
        private String taxRate;
        @JsonProperty("goodsTaxNo")
        private String goodsTaxNo;
        @JsonProperty("quantity")
        private String quantity;
        @JsonProperty("cargoName")
        private String cargoName;
        @JsonProperty("zeroTax")
        private String zeroTax;
        @JsonProperty("quantityUnit")
        private String quantityUnit;
        @JsonProperty("taxAmount")
        private String taxAmount;
        @JsonProperty("amountWithTax")
        private String amountWithTax;
    }
}
