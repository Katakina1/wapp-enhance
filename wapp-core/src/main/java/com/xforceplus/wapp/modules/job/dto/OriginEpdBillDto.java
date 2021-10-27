package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

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
    @Length(max = 20)
    private String taxCode;

    @ExcelProperty("Document Type")
    @Length(max = 20)
    private String documentType;

    @ExcelProperty("Reference key 1")
    @Length(max = 20)
    private String referenceKey1;

    @ExcelProperty("Cash disc. amt LC")
    @Length(max = 50)
    private String cashDiscAmtLc;

    @ExcelProperty("Account")
    @Length(max = 50)
    private String account;

    @ExcelProperty("Clearing date")
    @Length(max = 20)
    private String clearingDate;

    @ExcelProperty("Amount in local currency")
    @Length(max = 50)
    private String amountInLocalCurrency;

    @ExcelProperty("Reference key 2")
    @Length(max = 20)
    private String referenceKey2;

    @ExcelProperty("Reverse clearing")
    @Length(max = 20)
    private String reverseClearing;

    @ExcelProperty("Reference")
    @Length(max = 50)
    private String reference;

    @ExcelProperty("Payment Block")
    @Length(max = 20)
    private String paymentBlock;

    @ExcelProperty("Posting Date")
    @Length(max = 20)
    private String postingDate;

    @ExcelProperty("Invoice reference")
    @Length(max = 20)
    private String invoiceReference;

    @ExcelProperty("Payment date")
    @Length(max = 20)
    private String paymentDate;

    @ExcelProperty("Company Code")
    @Length(max = 20)
    private String companyCode;

    @ExcelProperty("Text")
    @Length(max = 20)
    private String text;

}
