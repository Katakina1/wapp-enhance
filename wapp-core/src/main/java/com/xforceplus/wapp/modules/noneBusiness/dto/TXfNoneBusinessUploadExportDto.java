package com.xforceplus.wapp.modules.noneBusiness.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TXfNoneBusinessUploadExportDto {

    @ExcelProperty(value = "发票号码", index = 0)
    private String invoiceNo;
    @ExcelProperty(value = "验真状态", index = 1)
    private String verifyStatus;
    @ExcelProperty(value = "ofd验签状态", index = 2)
    private String ofdStatus;
    @ExcelProperty(value = "门店号", index = 3)
    private String storeNo;
    @ExcelProperty(value = "业务类型", index = 4)
    private String bussinessType;
    @ExcelProperty(value = "发票类型", index = 5)
    private String invoiceType;
    @ExcelProperty(value = "上传ofd/pdf批次号", index = 6)
    private String batchNo;
    @ExcelProperty(value = "发票代码", index = 7)
    private String invoiceCode;

    @ExcelProperty(value = "失败原因", index = 8)
    private String reason;
    @ExcelProperty(value = "发票上传门店", index = 9)
    private String invoiceStoreNo;
    @ExcelProperty(value = "货物/服务发生期间", index = 10)
    private String storeDate;
    @ExcelProperty(value = "业务单号", index = 11)
    private String bussinessNo;
    @ExcelProperty(value = "创建人", index = 12)
    private String createUser;
    @ExcelProperty(value = "开票日期", index = 13)
    private String invoiceDate;
    @ExcelProperty(value = "公司代码", index = 14)
    private String companyCode;
    @ExcelProperty(value = "金额", index = 14)
    private String invoiceAmount;
    @ExcelProperty(value = "税额", index = 14)
    private String taxAmount;
    @ExcelProperty(value = "价税合计", index = 14)
    private String totalAmount;


}
