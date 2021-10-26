package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 原始EPD单数据
 *
 * @author Kenny Wong
 * @since 2021-10-15
 */
@Data
public class OriginEpdBillDto {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("Tax code")
    private String taxCode;

    @ExcelProperty("Document Type")
    private String documentType;

    @ExcelProperty("Reference key 1")
    private String referenceKey1;

    @ExcelProperty("Cash disc. amt LC")
    private String cashDiscAmtLc;

    @ExcelProperty("Account")
    private String account;

    @ExcelProperty("Clearing date")
    private String clearingDate;

    @ExcelProperty("Amount in local currency")
    private String amountInLocalCurrency;

    @ExcelProperty("Reference key 2")
    private String referenceKey2;

    @ExcelProperty("Reverse clearing")
    private String reverseClearing;

    @ExcelProperty("Reference")
    private String reference;

    @ExcelProperty("Payment Block")
    private String paymentBlock;

    @ExcelProperty("Posting Date")
    private String postingDate;

    @ExcelProperty("Invoice reference")
    private String invoiceReference;

    @ExcelProperty("Payment date")
    private String paymentDate;

    @ExcelProperty("Company Code")
    private String companyCode;

    @ExcelProperty("Text")
    private String text;

}
