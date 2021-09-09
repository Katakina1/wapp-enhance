package com.xforceplus.wapp.modules.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 费用扫描签收
 */
@Getter
@Setter
public class MaterialInvoiceQueryExcelEntity extends BaseRowModel implements Serializable {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    //供应商号
    @ExcelProperty(value={"供应商号"},index = 1)
    private String venderId;
    //供应商号
    @ExcelProperty(value={"供应商名字"},index = 2)
    private String venderName;

    //发票号码
    @ExcelProperty(value={"发票号码"},index = 3)
    private String invoiceNo;

    //开票日期
    @ExcelProperty(value={"发票日期"},index = 4)
    private String invoiceDate;
    //金额
    @ExcelProperty(value={"税率"},index = 5)
    private String tax;

    //税额
    @ExcelProperty(value={"税额"},index = 6)
    private String taxAmount;

    //税价合计
    @ExcelProperty(value={"价税合计"},index = 7)
    private String totalAmount;
    @ExcelProperty(value={"发票类型"},index = 8)
    private String invoiceType;
    //购方名称
    @ExcelProperty(value={"购货单位名称"},index = 9)
    private String gfName;
    //开票日期
    @ExcelProperty(value={"数据提交日期日期"},index = 10)
    private String matchDate;
    //开票日期
    @ExcelProperty(value={"实物发票处理日期"},index = 11)
    private String scanMatchDate;
}
