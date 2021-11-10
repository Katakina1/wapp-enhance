package com.xforceplus.wapp.modules.deduct.model;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SunShiyong on 2021/10/22.
 */
@Data
public class ExportEPDBillModel {
    /**
     * 业务单据编号
     */
    @ExcelProperty("协议号")
    private String businessNo;
    /**
     * 扣款公司jv_code
     */
    @ExcelProperty("扣款公司")
    private String purchaserNo;
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

    @ExcelProperty("供应商6D")
    private String agreementMemo;

    @ExcelProperty("协议类型")
    private String agreementReference;

    @ExcelProperty("协议类型编码")
    private String agreementReasonCode;

    @ExcelProperty("文档类型")
    private String agreementDocumentType;

    @ExcelProperty("文档编号")
    private String agreementDocumentNumber;

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

    /**
     * 含税金额
     */
    @ExcelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    /**
     * 税额
     */
    @ExcelProperty("税额")
    private BigDecimal taxAmount;

    @ExcelProperty("税码")
    private String agreementTaxCode;

    @ExcelProperty("发票类型")
    private String invoiceType;

    @ExcelProperty("入账日期")
    private Date verdictDate;


    @ExcelProperty("批次号")
    private String batchNo;



    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("红字信息表编号")
    private String redNotificationNo;

}
