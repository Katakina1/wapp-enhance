package com.xforceplus.wapp.modules.ngsInputInvoice.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ExportDto {

    @ExcelProperty("发票抵扣的所属税期")
    @ColumnWidth(25)
    private String taxPeriod;

    @ExcelProperty("公司代码")
    @ColumnWidth(25)
    private String companyCode;

    @ExcelProperty("jv")
    @ColumnWidth(20)
    private String jvCode;

    @ExcelProperty("发票代码")
    @ColumnWidth(25)
    private String invoiceCode;

    @ExcelProperty("发票号码")
    @ColumnWidth(25)
    private String invoiceNo;

    @ExcelProperty("扫描日期")
    @ColumnWidth(25)
    private Date scanTime;

    @ExcelProperty("供应商号码")
    @ColumnWidth(25)
    private String venderid;

    @ExcelProperty("供应商名称")
    @ColumnWidth(25)
    private String vendername;

    @ExcelProperty("店号")
    @ColumnWidth(25)
    private String costCenter;

    @ExcelProperty("税额")
    @ColumnWidth(25)
    private BigDecimal taxAmount;

    @ExcelProperty("税率")
    @ColumnWidth(25)
    private BigDecimal taxRate;

    @ExcelProperty("税码")
    @ColumnWidth(25)
    private String taxCode;

    @ExcelProperty("含税金额")
    @ColumnWidth(25)
    private BigDecimal amountWithTax;

    @ExcelProperty("不含税金额")
    @ColumnWidth(25)
    private BigDecimal amountWithoutTax;

    @ExcelProperty("凭证号")
    @ColumnWidth(25)
    private String voucherNo;

    @ExcelProperty("开票日期")
    @ColumnWidth(25)
    private String paperDrewDate;

    @ExcelProperty("换票后的发票号码")
    @ColumnWidth(25)
    private String newInvoiceNo;

    @ExcelProperty("备注")
    @ColumnWidth(25)
    private String remark;

    @ExcelProperty("票龄")
    @ColumnWidth(25)
    private String invoiceAge;

    @ExcelProperty("业务类型")
    @ColumnWidth(25)
    private String businessType;

    @ExcelProperty("扫描人")
    @ColumnWidth(25)
    private String scanUser;

    @ExcelProperty("购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-")
    @ColumnWidth(25)
    private String isImmovables;

    @ExcelProperty("大类")
    @ColumnWidth(25)
    private String largeCategory;

    @ExcelProperty("发票类型")
    @ColumnWidth(25)
    private String invoiceType;

    @ExcelProperty("uuid")
    @ColumnWidth(25)
    private String uuid;

    @ExcelProperty("购方税号")
    @ColumnWidth(25)
    private String gfTaxNo;

    @ExcelProperty("交接人")
    @ColumnWidth(25)
    private String handoverPerson;

    @ExcelProperty("费用类科目")
    @ColumnWidth(25)
    private String expenseSubject;

    @ExcelProperty("进项转出金额")
    @ColumnWidth(25)
    private BigDecimal inputOutAmount;

    @ExcelProperty("进项转出凭证")
    @ColumnWidth(25)
    private String inputOutputVoucher;

    @ExcelProperty("可抵扣的固定资产进项税金")
    @ColumnWidth(25)
    private BigDecimal kdkInputTaxAmount;
}