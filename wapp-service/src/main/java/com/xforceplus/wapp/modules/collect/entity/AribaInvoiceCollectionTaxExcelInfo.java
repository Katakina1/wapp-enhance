package com.xforceplus.wapp.modules.collect.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Ariba税务传票清单导出实体（抵账表）
 * @author Colin.hu
 * @date 4/11/2018
 */
@Getter @Setter @ToString
public class AribaInvoiceCollectionTaxExcelInfo extends BaseRowModel {


    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"JV"},index = 1)
    private String jvCode;
    @ExcelProperty(value={"发票代码"},index = 2)
    private String invoiceCode;
    @ExcelProperty(value={"发票号码"},index = 3)
    private String invoiceNo;
    @ExcelProperty(value={"供应商号"},index = 4)
    private String venderid;
    @ExcelProperty(value={"供应商名称"},index = 5)
    private String vendername;
    @ExcelProperty(value={"Store#"},index = 6)
    private String storeNo;
    @ExcelProperty(value={"税额"},index = 7)
    private String taxAmount;
    @ExcelProperty(value={"TaxCode"},index = 8)
    private String taxCode;
    @ExcelProperty(value={"税率"},index = 9)
    private String taxRate;
    @ExcelProperty(value={"价税合计"},index = 10)
    private String totalAmount;
    @ExcelProperty(value={"开票日期"},index = 11)
    private String invoiceDate;
    @ExcelProperty(value={"备注"},index = 12)
    private String remark;
    @ExcelProperty(value={"业务类型"},index = 13)
    private String serviceType;
    @ExcelProperty(value={"会计科目"},index = 14)
    private String glAccount;
    @ExcelProperty(value={"可抵扣的固定资产进项税金"},index = 15)
    private String dkTaxAmoumt;
    @ExcelProperty(value={"传票人ID"},index = 16)
    private String cpUserId;
    @ExcelProperty(value={"购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-"},index = 17)
    private String isOver;
    @ExcelProperty(value={"大类（指商品类，资产类，费用类）"},index = 18)
    private String flowType;
    @ExcelProperty(value={"MCC代码"},index = 19)
    private String mccCode;
    @ExcelProperty(value={"购方名称"},index = 20)
    private String gfName;
    @ExcelProperty(value={"购方税号"},index = 21)
    private String gfTaxNo;

}
