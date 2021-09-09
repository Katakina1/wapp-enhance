package com.xforceplus.wapp.modules.InformationInquiry.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 发票匹配
 */
@Setter
@Getter
public class SignForQueryExcel1Entity extends BaseRowModel {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;

    //发票代码
    @ExcelProperty(value={"发票代码"},index = 5)
    private String invoiceCode;
    //发票号码
    @ExcelProperty(value={"发票号码"},index = 6)
    private String invoiceNo;
    //开票日期
    @ExcelProperty(value={"开票日期"},index = 7)
    private String invoiceDate;

    //签收日期
    @ExcelProperty(value={"签收日期"},index = 3)
    private String qsDate;

    //签收状态
    @ExcelProperty(value={"签收状态"},index = 1)
    private String qsStatus;


    //购方名称
    @ExcelProperty(value={"购方名称"},index = 10)
    private String gfName;

    //销方名称
    @ExcelProperty(value={"销方名称"},index = 11)
    private String xfName;

    //金额
    @ExcelProperty(value={"金额"},index = 12)
    private String invoiceAmount;

    //税额
    @ExcelProperty(value={"税额"},index = 13)
    private String taxAmount;
    @ExcelProperty(value={"JVCODE"},index = 14)
    private String jvCode;
    @ExcelProperty(value={"COMPANYCODE"},index = 15)
    private String companyCode;

    private String invoiceType;
    @ExcelProperty(value={"供应商号"},index = 9)
    private  String venderid;
    @ExcelProperty(value={"签收描述"},index = 2)
    private String notes;
    @ExcelProperty(value={"业务类型"},index = 8)
    private String flowType;
    @ExcelProperty(value={"扫描匹配状态"},index = 16)
    private String scanMatchStatus;


    @ExcelProperty(value={"EPS_NO"},index = 17)
    private String epsNo;
    @ExcelProperty(value={"扫描匹配描述"},index = 18)
    private String scanFailReason;
    @ExcelProperty(value={"扫描流水号"},index = 4)
    private String scanId;


    
}
