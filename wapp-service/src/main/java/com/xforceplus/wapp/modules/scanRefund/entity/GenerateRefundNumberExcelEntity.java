package com.xforceplus.wapp.modules.scanRefund.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * 抵账表实体(发票签收)
 */
@Getter
@Setter
public class GenerateRefundNumberExcelEntity extends BaseRowModel {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;


    //发票代码
    @ExcelProperty(value={"发票代码"},index =5)
    private String invoiceCode;

    //发票号码
    @ExcelProperty(value={"发票号码"},index =7)
    private String invoiceNo;

    //开票日期
    @ExcelProperty(value={"开票日期"},index =8)
    private String invoiceDate;


    //金额
    @ExcelProperty(value={"金额"},index =9)
    private String invoiceAmount;

    //税额
    @ExcelProperty(value={"税额"},index =10)
    private String taxAmount;


    //供应商号
    @ExcelProperty(value={"供应商号"},index =4)
    private String venderId;

    //扫描流水号
    @ExcelProperty(value={"扫描流水号"},index =2)
    private String invoiceSerialNo;

    //扫描时间
    @ExcelProperty(value={"扫描时间"},index =1)
    private String createDate;


    //退票原因
    @ExcelProperty(value={"退票原因"},index =11)
    private  String refundReason;
    //费用退票编号
    @ExcelProperty(value={"退票编号"},index =12)
    private String refundCode;
    //费用生成单号
    @ExcelProperty(value={"EPS_NO"},index =6)
    private String epsNo;
    //属于
    @ExcelProperty(value={"申请类型"},index =3)
    private String belongsTo;



}
