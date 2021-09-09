package com.xforceplus.wapp.modules.scanRefund.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 抵账表实体(发票签收)
 */
@Getter
@Setter
public class EnterPackageNumberExcelEntity extends BaseRowModel {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;

    //发票代码
    @ExcelProperty(value={"发票代码"},index = 4)
    private String invoiceCode;

    //发票号码
    @ExcelProperty(value={"发票号码"},index = 5)
    private String invoiceNo;

    //开票日期
    @ExcelProperty(value={"开票日期"},index = 6)
    private String invoiceDate;

    //金额
    @ExcelProperty(value={"金额"},index = 7)
    private String invoiceAmount;

    //税额
    @ExcelProperty(value={"税额"},index = 8)
    private String taxAmount;

    //供应商号
    @ExcelProperty(value={"供应商号"},index = 3)
    private String venderId;

    //退单号
    @ExcelProperty(value={"退单号"},index = 2)
    private String rebateNo;

    //退单时间
    @ExcelProperty(value={"退单时间"},index = 1)
    private String rebateDate;


    @ExcelProperty(value={"EPS单号"},index = 9)
    private String epsNo;

    @ExcelProperty(value={"部门/门店"},index = 10)
    private String shopNo;

    @ExcelProperty(value={"申请人部门"},index = 11)
    private String applicantDepartment;

    @ExcelProperty(value={"申请人ID"},index = 12)
    private String applicantNo;
    @ExcelProperty(value={"申请人姓名"},index = 13)
    private String applicantName;

    @ExcelProperty(value={"申请人电话"},index = 14)
    private String applicantCall;

    @ExcelProperty(value={"分区"},index = 15)
    private String applicantSubarea;

    @ExcelProperty(value={"导入日期"},index = 16)
    private String importDate;
    
 
}
