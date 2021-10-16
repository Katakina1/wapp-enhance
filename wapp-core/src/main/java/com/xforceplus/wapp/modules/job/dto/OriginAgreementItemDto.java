package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 原始协议单明细1 SAP-ZARR0355原稿
 * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OriginAgreementItemDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("Amount With Tax")
    private String amountWithTax;

    @ExcelProperty("Quant")
    private String quant;

    @ExcelProperty("Customer Number")
    private String customerNumber;

    @ExcelProperty("Contents")
    private String contents;

    @ExcelProperty("Internal Invoice No.")
    private String internalInvoiceNo;

    @ExcelProperty("Profit Centre")
    private String profitCentre;

    @ExcelProperty("Unit Prices")
    private String unitPrices;

    @ExcelProperty("Measurements")
    private String measurements;

    @ExcelProperty("Memo")
    private String memo;

    @ExcelProperty("Reason Code")
    private String reasonCode;

    @ExcelProperty("SAP Accounting Document")
    private String sapAccountingDocument;

    @ExcelProperty("Customer")
    private String customer;

    @ExcelProperty("Sequence Number")
    private String sequenceNumber;

}
