package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * <p>
 * 原始EPD单LOG明细
 * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OriginEpdLogItemDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("Reference")
    private String reference;

    @ExcelProperty("Tax Rate")
    private String taxRate;

    @ExcelProperty("After Payment Block")
    private String afterPaymentBlock;

    @ExcelProperty("CoCd")
    private String cocd;

    @ExcelProperty("G/L")
    private String gl;

    @ExcelProperty("EPD Document Number")
    private String epdDocumentNumber;

    @ExcelProperty("EPD Company Code")
    private String epdCompanyCode;

    @ExcelProperty("Before Payment Block")
    private String beforePaymentBlock;

    @ExcelProperty("Year")
    private String year;

    @ExcelProperty("Discount Rate%")
    private String discountRate;

    @ExcelProperty("Time")
    private String time;

    @ExcelProperty("Vendor")
    private String vendor;

    @ExcelProperty("Profit Ctr")
    private String profitCtr;

    @ExcelProperty("User name")
    private String userName;

    @ExcelProperty("EPD Fiscal Year")
    private String epdFiscalYear;

    @ExcelProperty("DocumentNo")
    private String documentNo;

    @ExcelProperty("Cl.")
    private String cl;

    @ExcelProperty("Discount Amount")
    private BigDecimal discountAmount;

    @ExcelProperty("Amount")
    private BigDecimal amount;

    @ExcelProperty("Date in Format YYYYMMDD")
    private String dateInFormatYyyymmdd;

    @ExcelProperty("Status Message")
    private String statusMessage;

    @ExcelProperty("Doc. Type")
    private String docType;

    @ExcelProperty("Natural Number")
    private String naturalNumber;

    @ExcelProperty("Itm")
    private String itm;

    @ExcelProperty("EPD Corresponding Number")
    private String epdCorrespondingNumber;

    @ExcelProperty("Department Number")
    private String departmentNumber;

    @ExcelProperty("D/C")
    private String dc;

    @ExcelProperty("Status")
    private String status;
}
