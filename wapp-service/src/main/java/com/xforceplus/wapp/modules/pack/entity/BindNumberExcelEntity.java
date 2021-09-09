package com.xforceplus.wapp.modules.pack.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 发票签收实体
 * CreateBy leal.liang on 2018/4/12.
 **/
@Getter
@Setter
@ToString
public class BindNumberExcelEntity extends BaseRowModel implements Serializable {



    @ExcelProperty(value={"装订成册","ID"},index = 0)
    private String id;

    @ExcelProperty(value={"装订成册","扫描日期"},index = 1)
    private String scanDate;

    @ExcelProperty(value={"装订成册","扫描流水号"},index = 2)
    private String scanNo;

    @ExcelProperty(value={"装订成册","JV"},index = 3)
    private String jv;

    @ExcelProperty(value={"装订成册","公司代码"},index = 4)
    private String gsdm;

    @ExcelProperty(value={"装订成册","供应商号"},index = 5)
    private String venderNo;

    @ExcelProperty(value={"装订成册","供应商名称"},index = 6)
    private String venderName;

    @ExcelProperty(value={"装订成册","购方名称"},index = 7)
    private String gfName;

    @ExcelProperty(value={"装订成册","发票代码"},index = 8)
    private String invoiceCode;

    @ExcelProperty(value={"装订成册","发票号码"},index = 9)
    private String invoiceNo;

    @ExcelProperty(value={"装订成册","发票类型"},index = 10)
    private String invoiceType;

    @ExcelProperty(value={"装订成册","EPS_NO"},index = 11)
    private String epsNo;

    @ExcelProperty(value={"装订成册","发票金额"},index = 12)
    private String invoiceAmount;

    @ExcelProperty(value={"装订成册","税额"},index = 13)
    private String taxAmount;


    @ExcelProperty(value={"装订成册","开票日期"},index = 14)
    private String invoiceDate;

  
    @ExcelProperty(value={"装订成册","税率"},index = 15)
    private String taxRate;

    @ExcelProperty(value={"装订成册","业务类型"},index = 16)
    private String serviceType;

    @ExcelProperty(value={"装订成册","装订册号"},index = 17)
    private String bindNumber;


}
