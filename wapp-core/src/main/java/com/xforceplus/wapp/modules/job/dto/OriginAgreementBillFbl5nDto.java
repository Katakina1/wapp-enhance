package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 原始协议单数据SAP-FBL5N
 *
 * @author Kenny Wong
 * @since 2021-10-15
 */
@Data
public class OriginAgreementBillFbl5nDto {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("Cleared/open items symbol")
    @Length(max = 50)
    private String clearedOpenItemsSymbol;

    @ExcelProperty("Company Code")
    @Length(max = 20)
    private String companyCode;

    @ExcelProperty("Account")
    @Length(max = 20)
    private String account;

    @ExcelProperty("Document Number")
    @Length(max = 20)
    private String documentNumber;

    @ExcelProperty("Document Type")
    @Length(max = 20)
    private String documentType;

    @ExcelProperty("Reason code")
    @Length(max = 20)
    private String reasonCode;

    @ExcelProperty("Document Date")
    @Length(max = 20)
    private String documentDate;

    @ExcelProperty("Posting Date")
    @Length(max = 20)
    private String postingDate;

    @ExcelProperty("Clearing date")
    @Length(max = 20)
    private String clearingDate;

    @ExcelProperty("Reference key 2")
    @Length(max = 20)
    private String referenceKey2;

    @ExcelProperty("Reference")
    @Length(max = 20)
    private String reference;

    @ExcelProperty("Amount in doc. curr.")
    @Length(max = 50)
    private String amountInDocCurr;

    @ExcelProperty("Department")
    @Length(max = 20)
    private String department;

    @ExcelProperty("Document Header Text")
    private String documentHeaderText;

    @ExcelProperty("Text")
    private String text;

    @ExcelProperty(index = 15)
    @Length(max = 20)
    private String taxCode;

}
