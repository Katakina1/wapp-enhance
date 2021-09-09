package com.xforceplus.wapp.modules.collect.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 发票认证报告导出实体
 * @author Colin.hu
 * @date 4/11/2018
 */
@Getter @Setter @ToString
public class InvoiceCollectionResultExcelInfo extends BaseRowModel {


    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"认证状态"},index = 2)
    private String rzhYesorno;
    @ExcelProperty(value={"认证处理状态"},index = 1)
    private String authStatus;
    @ExcelProperty(value={"认证时间"},index = 3)
    private String rzhDate;
    @ExcelProperty(value={"认证人"},index = 4)
    private String confirmUser;
    @ExcelProperty(value={"税款所属期"},index = 5)
    private String rzhBelongDate;
    @ExcelProperty(value={"发票代码"},index = 6)
    private String invoiceCode;
    @ExcelProperty(value={"发票号码"},index = 7)
    private String invoiceNo;
    @ExcelProperty(value={"开票日期"},index = 8)
    private String invoiceDate;
    @ExcelProperty(value={"购方税号"},index = 9)
    private String gfTaxNo;
    @ExcelProperty(value={"购方名称"},index = 10)
    private String gfName;
    @ExcelProperty(value={"销方税号"},index = 11)
    private String xfTaxNo;

    @ExcelProperty(value={"销方名称"},index = 12)
    private String xfName;
    @ExcelProperty(value={"金额"},index = 13)
    private String invoiceAmount;
    @ExcelProperty(value={"税额"},index = 14)
    private String taxAmount;
    @ExcelProperty(value={"发票状态"},index = 15)
    private String invoiceStatus;
//    @ExcelProperty(value={"签收状态"},index = 17)
//    private String qsStatus;
//    @ExcelProperty(value={"签收日期"},index = 18)
//    private String qsDate;
    @ExcelProperty(value={"JV"},index = 16)
    private String jvCode;
    @ExcelProperty(value={"公司代码"},index = 17)
    private String companyCode;
    @ExcelProperty(value={"供应商号"},index = 18)
    private String venderid;
    @ExcelProperty(value={"认证返回信息"},index = 19)
    private String rzhBackMsg;
    @ExcelProperty(value={"业务类型"},index = 20)
    private String flowType;

}
