package com.xforceplus.wapp.modules.deduct.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.modules.deduct.service.InvoiceTypeConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SunShiyong on 2021/10/22.
 */
@Data
public class ExportAgreementBillModel {
    /**
     * 业务单据编号
     */
    @ExcelProperty("协议单号")
    private String businessNo;
    /**
     * 供应商编号
     */
    @ExcelProperty("供应商编号")
    private String sellerNo;
    /**
     * 供应商名称
     */
    @ExcelProperty("供应商名称")
    private String sellerName;
    /**
     * 扣款日期
     */
    @ExcelProperty("扣款日期")
    private Date deductDate;
    /**
     * 扣款公司
     */
    @ExcelProperty("扣款公司")
    private String purchaserName;

    @ExcelProperty("协议供应商6D")
    private String agreementMemo;

    @ExcelProperty("协议类型")
    private String agreementReference;

    @ExcelProperty("协议类型编码")
    private String agreementReasonCode;

    @ExcelProperty("文档类型")
    private String agreementDocumentType;

    @ExcelProperty("文档编号")
    private String agreementDocumentNumber;

    @ExcelProperty("税码")
    private String agreementTaxCode;

    /**
     * 含税金额
     */
    @ExcelProperty("含税金额")
    private BigDecimal amountWithTax;
    /**
     * 税率
     */
    @ExcelProperty("税率")
    private BigDecimal taxRate;

    @ExcelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    @ExcelProperty("税额")
    private BigDecimal taxAmount;

    @ExcelProperty("入账日期")
    private Date verdictDate;

    @ExcelProperty("批次号")
    private String batchNo;

    @ExcelProperty(value = "发票类型",converter = InvoiceTypeConverter.class)
    private String invoiceType;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("红字信息表编号")
    private String redNotificationNo;


}
