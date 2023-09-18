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
public class OriginAgreementBillZarrDto {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("Sequence Number")
    @Length(max = 20)
    private String sequenceNumber;

    @ExcelProperty("Internal Invoice No.")
    @Length(max = 50)
    private String internalInvoiceNo;

    @ExcelProperty("Customer Number")
    @Length(max = 20)
    private String customerNumber;

    @ExcelProperty("Customer")
    @Length(max = 100)
    private String customer;

    @ExcelProperty("Contents")
    @Length(max = 100)
    private String contents;

    @ExcelProperty("Quant")
    @Length(max = 20)
    private String quant;

    @ExcelProperty("Measurements")
    private String measurements;

    @ExcelProperty("Unit Prices")
    @Length(max = 20)
    private String unitPrices;

    @ExcelProperty("Amount With Tax")
    @Length(max = 50)
    private String amountWithTax;

    @ExcelProperty("Memo")
    @Length(max = 100)
    private String memo;

    @ExcelProperty("Profit Centre")
    @Length(max = 20)
    private String profitCentre;

    @ExcelProperty("SAP Accounting Document")
    @Length(max = 20)
    private String sapAccountingDocument;

    @ExcelProperty(index = 12)
    @Length(max = 20)
    private String reasonCode;

}
