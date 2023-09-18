package com.xforceplus.wapp.modules.noneBusiness.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringStringConverter;
import com.xforceplus.wapp.modules.noneBusiness.convert.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TXfNoneBusinessUploadExportDto {

    @ExcelProperty(value = "上传日期")
    private Date createTime;
    @ExcelProperty(value = "电票流水号")
    private String batchNo;
    @ExcelProperty(value = "业务类型", converter = BusinessTypeConver.class)
    private String bussinessType;
    @ExcelProperty(value = "费用承担店号")
    private String storeNo;
    @ExcelProperty(value = "业务单号")
    private String bussinessNo;
    @ExcelProperty(value = "验真状态", converter = VerifyStatusConver.class)
    private String verifyStatus;
    @ExcelProperty(value = "验签状态", converter = OfdStatusConver.class)
    private String ofdStatus;
    @ExcelProperty(value = "验真失败备注")
    private String reason;
    @ExcelProperty(value = "JV")
    private String companyCode;
    @ExcelProperty(value = "公司代码")
    private String companyNo;
    @ExcelProperty(value = "供应商号")
    private String supplierId;
    @ExcelProperty(value = "功能组", converter = InvoiceTypeStatusConver.class)
    private String invoiceType;
    @ExcelProperty(value = "发票上传门店")
    private String invoiceStoreNo;
    @ExcelProperty(value = "货物/服务发生期间-开始")
    private String storeStart;
    @ExcelProperty(value = "货物/服务发生期间-结束")
    private String storeEnd;
    @ExcelProperty(value = "发票代码")
    private String invoiceCode;
    @ExcelProperty(value = "发票号码")
    private String invoiceNo;
    @ExcelProperty(value = "发票类型", converter = InvoiceTypeConver.class)
    private String fpInvoiceType;
    @ExcelProperty(value = "开票日期")
    private String invoiceDate;
    @ExcelProperty(value = "购方税号")
    private String purTaxNo;
    @ExcelProperty(value = "购方名称")
    private String purTaxName;
    @ExcelProperty(value = "销方税号")
    private String sellerTaxNo;
    @ExcelProperty(value = "销方名称")
    private String sellerTaxName;
    @ExcelProperty(value = "金额", converter = AmountConverter.class)
    private String invoiceAmount;
    @ExcelProperty(value = "税额", converter = AmountConverter.class)
    private String taxAmount;
    @ExcelProperty(value = "价税合计",converter = AmountConverter.class)
    private String totalAmount;
    @ExcelProperty(value = "税率", converter = TaxRateConverter.class)
    private String taxRate;
    @ExcelProperty(value = "发票状态", converter = InvoiceStatusConver.class)
    private String invoiceStatus;
    @ExcelProperty(value = "认证状态", converter = AuthStatusConver.class)
    private String authStatus;
    @ExcelProperty(value = "认证日期")
    private String authDate;
    @ExcelProperty(value = "发票备注", converter = StringStringConverter.class)
    private String invoiceRemark;
    @ExcelProperty(value = "上传备注")
    private String remark;
    @ExcelProperty(value = "凭证号")
    private String voucherNo;
    @ExcelProperty(value = "入账日期")
    private String entryDate;
    @ExcelProperty(value = "创建人")
    private String createUser;
    @ExcelProperty(value = "税码")
    private String taxCode;
    /**
     * 货物或应税劳务名称
     */
    @ExcelProperty(value = "货物或应税劳务名称")
    private String goodsName;

}
