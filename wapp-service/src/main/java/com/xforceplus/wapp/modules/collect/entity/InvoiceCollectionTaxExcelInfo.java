package com.xforceplus.wapp.modules.collect.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 税务传票清单导出实体（抵账表）
 * @author Colin.hu
 * @date 4/11/2018
 */
@Getter @Setter @ToString
public class InvoiceCollectionTaxExcelInfo extends BaseRowModel {


    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"公司代码"},index = 1)
    private String companyCode;
    @ExcelProperty(value={"JV"},index = 2)
    private String jvCode;
    @ExcelProperty(value={"发票代码"},index = 3)
    private String invoiceCode;
    @ExcelProperty(value={"发票号码"},index = 4)
    private String invoiceNo;
    @ExcelProperty(value={"传票日期"},index = 5)
    private String cpDate;
    @ExcelProperty(value={"扫描日期"},index = 6)
    private String qsDate;
    @ExcelProperty(value={"供应商号"},index = 7)
    private String venderid;
    @ExcelProperty(value={"供应商名称"},index = 8)
    private String vendername;
    @ExcelProperty(value={"Store#"},index = 9)
    private String storeNo;
    @ExcelProperty(value={"税额"},index = 10)
    private String taxAmount;
    @ExcelProperty(value={"TaxCode"},index = 11)
    private String taxCode;
    @ExcelProperty(value={"税率"},index = 12)
    private String taxRate;
    @ExcelProperty(value={"价税合计"},index = 13)
    private String totalAmount;
    @ExcelProperty(value={"Voucher#"},index = 14)
    private String certificateNo;
    @ExcelProperty(value={"开票日期"},index = 15)
    private String invoiceDate;
    @ExcelProperty(value={"新发票号码"},index = 16)
    private String newInvoiceNo;
    @ExcelProperty(value={"备注"},index = 17)
    private String remark;
    @ExcelProperty(value={"票龄"},index = 18)
    private String pl;
    @ExcelProperty(value={"业务类型"},index = 19)
    private String serviceType;
    @ExcelProperty(value={"组别"},index = 20)
    private String zb;
    @ExcelProperty(value={"费用类科目"},index = 21)
    private String km;
    @ExcelProperty(value={"成本金额"},index = 22)
    private String invoiceAmount;
    @ExcelProperty(value={"进项税传出凭证"},index = 23)
    private String zcpz;
    @ExcelProperty(value={"可抵扣固定资产进项税金"},index = 24)
    private String jxsj;
    @ExcelProperty(value={"扫描人"},index = 25)
    private String scanName;
    @ExcelProperty(value={"购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-"},index = 26)
    private String isOver;
    @ExcelProperty(value={"大类（指商品类，资产类，费用类）"},index = 27)
    private String flowType;

    @ExcelProperty(value={"购方名称"},index = 28)
    private String gfName;
    @ExcelProperty(value={"购方税号"},index = 29)
    private String gfTaxNo;

    @ExcelProperty(value={"MCC代码"},index = 30)
    private String mccCode;
    @ExcelProperty(value={"EpsNo"},index = 31)
    private String epsNo;
    @ExcelProperty(value={"税款所属期"},index = 32)
    private String rzhBelongDate;
    @ExcelProperty(value={"发票类型"},index = 33)
    private String invoiceType;
    @ExcelProperty(value={"GL发票"},index = 34)
    private String gl;

}
