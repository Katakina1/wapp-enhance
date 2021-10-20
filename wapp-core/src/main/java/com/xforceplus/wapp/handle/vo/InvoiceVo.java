package com.xforceplus.wapp.handle.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@NoArgsConstructor
@Data
public class InvoiceVo {
    private Invoice data;

    @NoArgsConstructor
    @Data
    public static class Invoice {
        private String invoiceCode;
        private String invoiceNo;
        @JSONField(format = "yyyy-MM-dd")
        private Date paperDrewDate;
        private String purchaserName;
        private String purchaserTaxNo;
        private String purchaserAddrTel;
        private String purchaserBankNameAccount;
        private String checkCode;
        private String machineCode;
        private String cipherText;
        private BigDecimal amountWithTax;
        private BigDecimal taxAmount;
        private BigDecimal amountWithoutTax;
        private String sellerName;
        private String sellerTaxNo;
        private String sellerAddrTel;
        private String sellerBankNameAccount;
        private String cashierName;
        private String checkerName;
        private String invoicerName;
        private String remark;
        private String taxRate;
        private String status;
        private String invoiceColor;
        private String redFlag;
        private String taxCategory;
        private String industryIssueType;
        private String invoiceMedium;
        private String authStatus;
        private String authAfterStatus;
        private String authSyncStatus;
        private String authTaxPeriod;
        private String authUse;
        private String authBussiDate;
        private BigDecimal effectiveTaxAmount;
        private BigDecimal remainingAmount;
        @JsonProperty("purchaserInvoiceItemVOList")
        @JSONField(name = "purchaserInvoiceItemVOList")
        private List<InvoiceItemVO> items;
    }

    @NoArgsConstructor
    @Data
    public static class InvoiceItemVO {
        private String invoiceCode;
        private String invoiceNo;
        private String cargoCode;
        private String cargoName;
        private String itemSpec;
        private String quantityUnit;
        private String quantity;
        private String taxRate;
        private String unitPrice;
        private BigDecimal amountWithTax;
        private BigDecimal taxAmount;
        private BigDecimal amountWithoutTax;
        private String goodsTaxNo;
        private String plateNumber;
        private String tollStartDate;
        private String tollEndDate;
    }
}
