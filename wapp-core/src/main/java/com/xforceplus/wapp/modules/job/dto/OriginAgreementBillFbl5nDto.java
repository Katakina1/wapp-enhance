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

    @ExcelProperty("客户编码")
    @Length(max = 20)
    private String customerNumber;

    @ExcelProperty("客户名称")
    @Length(max = 100)
    private String customerName;

    @ExcelProperty("金额(含税)")
    @Length(max = 50)
    private String amountWithTax;

    @ExcelProperty("协议类型编码")
    @Length(max = 20)
    private String reasonCode;

    @ExcelProperty("协议号")
    @Length(max = 20)
    private String reference;

    @ExcelProperty("税码")
    @Length(max = 20)
    private String taxCode;

    @ExcelProperty("扣款日期")
    @Length(max = 20)
    private String clearingDate;

    @ExcelProperty("税率")
    @Length(max = 20)
    private String taxRate;

    @ExcelProperty("供应商6D")
    @Length(max = 20)
    private String memo;

    @ExcelProperty("协议类型")
    @Length(max = 50)
    private String referenceType;

    @ExcelProperty("扣款公司编码")
    @Length(max = 20)
    private String companyCode;

    @ExcelProperty("凭证编号")
    @Length(max = 20)
    private String documentNumber;

    @ExcelProperty("凭证类型")
    @Length(max = 20)
    private String documentType;

    @ExcelProperty("入账日期")
    @Length(max = 20)
    private String postingDate;

    @ExcelProperty("税额")
    @Length(max = 20)
    private String taxAmount;
}
