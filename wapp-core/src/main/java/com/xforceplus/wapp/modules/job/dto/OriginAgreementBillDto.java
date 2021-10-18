package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 原始协议单数据SAP-FBL5N
 * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OriginAgreementBillDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("Amount in doc. curr.")
    private String amountInDocCurr;

    @ExcelProperty("Text")
    private String text;

    @ExcelProperty("Clearing date")
    private String clearingDate;

    @ExcelProperty("Cleared/open items symbol")
    private String clearedOpenItemsSymbol;

    @ExcelProperty("Document Number")
    private String documentNumber;

    @ExcelProperty("Document Date")
    private String documentDate;

    @ExcelProperty("Tax code")
    private String taxCode;

    @ExcelProperty("Reference key 2")
    private String referenceKey2;

    @ExcelProperty("Department")
    private String department;

    @ExcelProperty("Company Code")
    private String companyCode;

    @ExcelProperty("Posting Date")
    private String postingDate;

    @ExcelProperty("Document Type")
    private String documentType;

    @ExcelProperty("Reference")
    private String reference;

    @ExcelProperty("Document Header Text")
    private String documentHeaderText;

    @ExcelProperty("Reason code")
    private String reasonCode;

    @ExcelProperty("Account")
    private String account;
}
