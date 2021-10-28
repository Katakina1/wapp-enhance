package com.xforceplus.wapp.modules.rednotification.model.excl;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// 申请流水号	发票类型	销方编号	销方名称	销方税号	购方编号	购方名称	购方税号	价格方式
// 税收分类编码	货物及服务名称	数量单位	型号规格	数量	税率	含税单价	不含税单价	不含税金额	税额	含税金额	原发票类型	原发票号码	原发票代码	原发票开票日期
// 结算单号	申请人	联系电话	申请理由	成品油申请原因	是否享受税收优惠政策	享受税收优惠政策内容	零税率标志	扣除额
@Getter
@Setter
public class ImportInfo extends BaseRowModel {
    @ExcelProperty(value = "申请流水号", index = 0)
    private String sellerNumber;
    @ExcelProperty(value = "发票类型", index = 1)
    private String invoiceType;
//    @ExcelProperty(value = "销方编号", index = 2)
//    private String sellerNo;
    @ExcelProperty(value = "销方名称", index = 2)
    private String sellerName;
    @ExcelProperty(value = "销方税号", index = 3)
    private String sellerTaxNo;

//    @ExcelProperty(value = "购方编号", index = 5)
//    private String purchaserNo;
    @ExcelProperty(value = "购方名称", index = 4)
    private String purchaserName;
    @ExcelProperty(value = "购方税号",index = 5)
    private String purchaserTaxNo;

    @ExcelProperty(value = "价格方式", index = 6)
    private String priceMethod;

    @ExcelProperty(value = "申请类型", index = 7)
    private Integer applyType;
    //===========明细==========

    @ExcelProperty(value = "税收分类编码", index = 8)
    private String goodsTaxNo;

    @ExcelProperty(value = "货物及服务名称", index = 9)
    private String goodsName;

    @ExcelProperty(value = "数量单位", index = 10)
    private String unit;

    @ExcelProperty(value = "规格型号", index = 11)
    private String model;

    @ExcelProperty(value = "数量", index = 12)
    private String num;

    @ExcelProperty(value = "税率", index = 13)
    private String taxRate;


    @ExcelProperty(value = "不含税单价", index = 14)
    private String unitPriceWithTax;

    @ExcelProperty(value = "不含税单价", index = 15)
    private String unitPrice;


    @ExcelProperty(value = "不含税金额", index = 16)
    private BigDecimal amountWithoutTax;

    @ExcelProperty(value = "税额", index = 17)
    private BigDecimal taxAmount;

    @ExcelProperty(value = "含税金额", index = 18)
    private BigDecimal amountWithTax;

    @ExcelProperty(value = "原发票类型", index = 19)
    private String originInvoiceType;

    @ExcelProperty(value = "原发票号码", index = 20)
    private String originInvoiceNo;

    @ExcelProperty(value = "原发票代码", index = 21)
    private String originInvoiceCode;

    @ExcelProperty(value = "原开票日期", index = 22)
    private String invoiceDate;

    // 结算单号	申请人	联系电话	申请理由	成品油申请原因	是否享受税收优惠政策	享受税收优惠政策内容	零税率标志	扣除额
    @ExcelProperty(value = "结算单号", index = 23)
    private String billNo;
    @ExcelProperty(value = "申请人", index = 24)
    private String userName;
    @ExcelProperty(value = "联系电话", index = 25)
    private String tel;
    @ExcelProperty(value = "申请理由", index = 26)
    private String applyReason;
    @ExcelProperty(value = "成品油申请原因", index = 27)
    private String oliApplyReason;
    @ExcelProperty(value = "是否享受税收优惠政策", index = 28)
    private String taxPre;
    @ExcelProperty(value = "享受税收优惠政策内容", index = 29)
    private String taxPreCon;
    @ExcelProperty(value = "零税率标志", index = 30)
    private String zeroTax;
    @ExcelProperty(value = "扣除额", index = 31)
    private BigDecimal deduction;

   // 补充额外字段
   @JsonIgnore
   private Integer specialInvoiceFlag;
    // 补充额外字段 版本号
    @JsonIgnore
    private String goodsNoVer;









}
