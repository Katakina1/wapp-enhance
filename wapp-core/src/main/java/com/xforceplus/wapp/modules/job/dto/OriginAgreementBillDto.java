package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 原始协议单数据SAP-FBL5N
 *
 * @author Kenny Wong
 * @since 2021-10-15
 */
@Data
public class OriginAgreementBillDto {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("客户编码")
    private String customerNumber;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("金额(含税)")
    private String amountWithTax;

    @ExcelProperty("协议类型编码")
    private String reasonCode;

    @ExcelProperty("协议号")
    private String reference;

    @ExcelProperty("税码")
    private String taxCode;

    @ExcelProperty("扣款日期")
    private String clearingDate;

    @ExcelProperty("税率")
    private String taxRate;

    @ExcelProperty("供应商6D")
    private String memo;

    @ExcelProperty("协议类型")
    private String referenceType;

    @ExcelProperty("扣款公司编码")
    private String companyCode;

    @ExcelProperty("凭证编号")
    private String documentNumber;

    @ExcelProperty("凭证类型")
    private String documentType;

    @ExcelProperty("入账日期")
    private String postingDate;

    @ExcelProperty("税额")
    private String taxAmount;
}
