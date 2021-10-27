package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 原始EPD单LOG明细
 *
 * @author Kenny Wong
 * @since 2021-10-15
 */
@Data
public class OriginEpdLogItemDto {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("Reference")
    @Length(max = 20)
    private String reference;

    @ExcelProperty("Tax Rate")
    @Length(max = 10)
    private String taxRate;

    @ExcelProperty("After Payment Block")
    @Length(max = 20)
    private String afterPaymentBlock;

    @ExcelProperty("CoCd")
    @Length(max = 20)
    private String cocd;

    @ExcelProperty("G/L")
    @Length(max = 20)
    private String gl;

    @ExcelProperty("EPD Document Number")
    @Length(max = 20)
    private String epdDocumentNumber;

    @ExcelProperty("EPD Company Code")
    @Length(max = 20)
    private String epdCompanyCode;

    @ExcelProperty("Before Payment Block")
    @Length(max = 20)
    private String beforePaymentBlock;

    @ExcelProperty("Year")
    @Length(max = 20)
    private String year;

    @ExcelProperty("Discount Rate%")
    @Length(max = 10)
    private String discountRate;

    @ExcelProperty("Time")
    @Length(max = 20)
    private String time;

    @ExcelProperty("Vendor")
    @Length(max = 20)
    private String vendor;

    @ExcelProperty("Profit Ctr")
    @Length(max = 20)
    private String profitCtr;

    @ExcelProperty("User name")
    @Length(max = 20)
    private String userName;

    @ExcelProperty("EPD Fiscal Year")
    @Length(max = 20)
    private String epdFiscalYear;

    @ExcelProperty("DocumentNo")
    @Length(max = 20)
    private String documentNo;

    @ExcelProperty("Cl.")
    @Length(max = 20)
    private String cl;

    @ExcelProperty("Discount Amount")
    @Length(max = 50)
    private String discountAmount;

    @ExcelProperty("Amount")
    @Length(max = 50)
    private String amount;

    @ExcelProperty("Date in Format YYYYMMDD")
    @Length(max = 20)
    private String dateInFormatYyyymmdd;

    @ExcelProperty("Status Message")
    @Length(max = 100)
    private String statusMessage;

    @ExcelProperty("Doc. Type")
    @Length(max = 20)
    private String docType;

    @ExcelProperty("Natural Number")
    @Length(max = 20)
    private String naturalNumber;

    @ExcelProperty("Itm")
    @Length(max = 20)
    private String itm;

    @ExcelProperty("EPD Corresponding Number")
    @Length(max = 20)
    private String epdCorrespondingNumber;

    @ExcelProperty("Department Number")
    @Length(max = 20)
    private String departmentNumber;

    @ExcelProperty("D/C")
    @Length(max = 20)
    private String dc;

    @ExcelProperty("Status")
    @Length(max = 20)
    private String status;
}
