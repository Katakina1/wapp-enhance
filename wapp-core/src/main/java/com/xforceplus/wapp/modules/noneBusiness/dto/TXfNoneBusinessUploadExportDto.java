package com.xforceplus.wapp.modules.noneBusiness.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.modules.noneBusiness.convert.*;
import lombok.Data;


@Data
public class TXfNoneBusinessUploadExportDto {

    @ExcelProperty(value = "业务单号", index = 0)
    private String bussinessNo;
    @ExcelProperty(value = "公司代码", index = 1)
    private String companyCode;
    @ExcelProperty(value = "门店号", index = 2)
    private String storeNo;
    @ExcelProperty(value = "发票上传门店", index = 3)
    private String invoiceStoreNo;
    @ExcelProperty(value = "货物/服务发生期间-开始", index = 4)
    private String storeStart;

    @ExcelProperty(value = "货物/服务发生期间-结束", index = 5)
    private String storeEnd;
    @ExcelProperty(value = "业务类型", index = 6, converter = BusinessTypeConver.class)
    private String bussinessType;
    @ExcelProperty(value = "发票类型", index = 7)
    private String invoiceType;
    @ExcelProperty(value = "上传ofd/pdf批次号", index = 8)
    private String batchNo;
    @ExcelProperty(value = "发票代码", index = 9)
    private String invoiceCode;
    @ExcelProperty(value = "发票号码", index = 10)
    private String invoiceNo;
    @ExcelProperty(value = "验真状态", index = 11, converter = VerifyStatusConver.class)
    private String verifyStatus;
    @ExcelProperty(value = "ofd验签状态", index = 12, converter = OfdStatusConver.class)
    private String ofdStatus;
    @ExcelProperty(value = "失败原因", index = 13)
    private String reason;
    @ExcelProperty(value = "开票日期", index = 14)
    private String invoiceDate;
    @ExcelProperty(value = "金额", index = 15)
    private String invoiceAmount;
    @ExcelProperty(value = "税额", index = 16)
    private String taxAmount;
    @ExcelProperty(value = "价税合计", index = 17)
    private String totalAmount;
    @ExcelProperty(value = "购方税号", index = 18)
    private String purTaxNo;
    @ExcelProperty(value = "购方名称", index = 19)
    private String purTaxName;
    @ExcelProperty(value = "销方税号", index = 20)
    private String sellerTaxNo;
    @ExcelProperty(value = "销方名称", index = 21)
    private String sellerTaxName;
    @ExcelProperty(value = "税率", index = 22)
    private String taxRate;
    @ExcelProperty(value = "发票状态", index = 23, converter = InvoiceStatusConver.class)
    private String invoiceStatus;
    @ExcelProperty(value = "认证状态", index = 24, converter = AuthStatusConver.class)
    private String authStatus;
    @ExcelProperty(value = "认证日期", index = 25)
    private String authDate;
    @ExcelProperty(value = "sap编号", index = 26)
    private String sap;
    @ExcelProperty(value = "创建人", index = 27)
    private String createUser;


}
