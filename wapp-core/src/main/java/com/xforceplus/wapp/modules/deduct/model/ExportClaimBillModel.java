package com.xforceplus.wapp.modules.deduct.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SunShiyong on 2021/10/22.
 */
@Data
public class ExportClaimBillModel {

    /**
     * 业务单据编号
     */
    @ExcelProperty("索赔单号")
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
     * 扣款公司jv_code
     */
    @ExcelProperty("扣款公司")
    private String purchaserNo;

    @ExcelProperty("协议供应商6D")
    private String agreementMemo;

    @ExcelProperty("协议类型")
    private String agreementDocumentType;

    @ExcelProperty("协议类型编码")
    private String agreementReasonCode;

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

    @ExcelProperty("定案日期")
    private Date verdictDate;

    @ExcelProperty("批次号")
    private String batchNo;

    @ExcelProperty("发票类型")
    private String invoiceType;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("红字信息表编号")
    private String redNotificationNo;

}
