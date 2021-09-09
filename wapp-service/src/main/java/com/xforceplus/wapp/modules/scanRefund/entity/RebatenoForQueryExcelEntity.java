package com.xforceplus.wapp.modules.scanRefund.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 发票匹配
 */
@Getter
@Setter
public class RebatenoForQueryExcelEntity extends BaseRowModel {

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
    private String createDate;

    //签收日期
    @ExcelProperty(value={"签收日期"},index = 2)
    private String qsDate;

    //签收状态
    @ExcelProperty(value={"签收状态"},index = 1)
    private String qsStatus;


    //购方名称
    @ExcelProperty(value={"购方名称"},index = 8)
    private String gfName;

    //销方名称
    @ExcelProperty(value={"销方名称"},index = 9)
    private String xfName;

    //金额
    @ExcelProperty(value={"金额"},index = 10)
    private String invoiceAmount;

    //税额
    @ExcelProperty(value={"税额"},index = 11)
    private String taxAmount;
    @ExcelProperty(value={"JVCODE"},index = 12)
    private String jvCode;
    @ExcelProperty(value={"COMPANYCODE"},index = 13)
    private String companyCode;

    private String invoiceType;
    @ExcelProperty(value={"供应商号"},index = 7)
    private  String venderid;
    @ExcelProperty(value={"描述"},index = 14)
    private String notes;

    //退单号
    @ExcelProperty(value={"退单号"},index = 15)
    private  String rebateNo;

    //邮包号
    @ExcelProperty(value={"邮包号"},index = 16)
    private String rebateExpressno;

    //退单时间
    @ExcelProperty(value={"退单时间"},index = 20)
    private String rebateDate;
    @ExcelProperty(value={"业务类型"},index = 19)
    private String flowType;
    @ExcelProperty(value={"EPS_NO"},index = 3)
    private String epsNo;
    @ExcelProperty(value={"邮寄公司"},index = 17)
    private String mailCompany;
    @ExcelProperty(value={"邮寄时间"},index = 18)
    private String mailDate;


}
