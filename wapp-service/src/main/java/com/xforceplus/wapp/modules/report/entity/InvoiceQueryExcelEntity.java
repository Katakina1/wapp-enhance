package com.xforceplus.wapp.modules.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 抵账表实体(发票签收)
 */
@Getter
@Setter
public class InvoiceQueryExcelEntity extends BaseRowModel implements Serializable {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;


    //发票代码
    @ExcelProperty(value={"发票代码"},index = 1)
    private String invoiceCode;

    //发票号码
    @ExcelProperty(value={"发票号码"},index = 2)
    private String invoiceNo;

    //开票日期
    @ExcelProperty(value={"开票日期"},index = 3)
    private String invoiceDate;

    //购方税号
    @ExcelProperty(value={"购方税号"},index = 4)
    private String gfTaxNo;

    //购方名称
    @ExcelProperty(value={"购方名称"},index = 5)
    private String gfName;

    //销方税号
    @ExcelProperty(value={"销方税号"},index = 6)
    private String xfTaxNo;

    //销方名称
    @ExcelProperty(value={"销方名称"},index = 7)
    private String xfName;

    //金额
    @ExcelProperty(value={"金额"},index = 8)
    private String invoiceAmount;

    //税额
    @ExcelProperty(value={"税额"},index = 9)
    private String taxAmount;

    //税价合计
    @ExcelProperty(value={"价税合计"},index = 15)
    private String totalAmount;



    //发票状态
    @ExcelProperty(value={"发票状态"},index = 10)
    private String invoiceStatus;


    //税款所属期
    @ExcelProperty(value={"税款所属期"},index = 16)
    private String rzhBelongDate;




    //匹配状态
    @ExcelProperty(value={"匹配状态"},index = 11)
    private  String dxhyMatchStatus;

    //匹配日期
    @ExcelProperty(value={"匹配日期"},index = 12)
    private String matchDate;

    //host状态
    @ExcelProperty(value={"HOST状态"},index = 13)
    private  String hostStatus;
    //扫描流水号
    @ExcelProperty(value={"扫描流水号"},index = 14)
    private  String scanningSeriano;
    //装订册号
    @ExcelProperty(value={"装订册号"},index = 15)
    private  String bbindingno;
    //装箱号
    @ExcelProperty(value={"装箱号"},index = 17)
    private  String packingno;


}
