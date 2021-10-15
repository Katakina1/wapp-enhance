package com.xforceplus.wapp.modules.rednotification.model.excl;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
@ApiModel("红字信息表主信息")
public class RedNotificationMain  extends BaseRowModel {

    @ApiModelProperty("备注")
    @ExcelProperty(value = "备注")
    private String remark;

    @ApiModelProperty("1 销方 2购方")
    @ExcelProperty(value = "申请方")
    private Integer userRole;

    @ApiModelProperty("申请类型 购方发起:0-已抵扣1-未抵扣 销方发起:2-开票有误")
    @ExcelProperty(value = "申请类型")
    private Integer applyType;

    @ApiModelProperty("申请原因")
    @ExcelProperty(value = "申请原因")
    private String applyReason;


    @ApiModelProperty("发票类型 c增值税普通发票 s 增值税专用发票 se 增值税电子专用发票 ce 增值税电子普通发票")
    @ExcelProperty(value = "发票类型")
    private String invoiceType;

    @ApiModelProperty("原发票类型 c增值税普通发票 s 增值税专用发票 se 增值税电子专用发票 ce 增值税电子普通发票")
    @ExcelProperty(value = "原发票类型")
    private String originInvoiceType;

    @ApiModelProperty("原发票代码")
    @ExcelProperty(value = "原发票代码")
    private String originalInvoiceCode;

    @ApiModelProperty("原发票号码")
    @ExcelProperty(value = "原发票号码")
    private String originalInvoiceNo;

    @ApiModelProperty("原开票日期")
    @ExcelProperty(value = "原开票日期")
    private String originalInvoiceDate;


    /**
     * 购方税号
     */
    @ApiModelProperty("扣款公司税号")
    @ExcelProperty(value = "扣款公司税号")
    private String purchaserTaxNo;

    /**
     * 购方名称
     */
    @ApiModelProperty("扣款公司名称")
    @ExcelProperty(value = "扣款公司名称")
    private String purchaserName;

    /**
     * 销方税号
     */
    @ApiModelProperty("供应商税号")
    @ExcelProperty(value = "供应商税号")
    private String sellerTaxNo;

    /**
     * 销方名称
     */
    @ApiModelProperty("供应商名称")
    @ExcelProperty(value = "供应商名称")
    private String sellerName;

    @ApiModelProperty("不含税金额")
    @ExcelProperty(value = "供应商名称")
    private BigDecimal amountWithoutTax;

    @ApiModelProperty("含税金额")
    @ExcelProperty(value = "供应商名称")
    private BigDecimal amountWithTax;

    @ApiModelProperty("税额")
    @ExcelProperty(value = "供应商名称")
    private BigDecimal taxAmount;


    @ApiModelProperty("单号")
    @ExcelProperty(value = "供应商名称")
    private String billNo;

    /**
     * 红字信息来源1.索赔单，2协议单，3.EPD
     */
    @ApiModelProperty("红字信息来源1.索赔单，2协议单，3.EPD")
    @ExcelProperty(value = "供应商名称")
    private Integer invoiceOrigin;

    /**
     * 公司编号
     */
    @ApiModelProperty("公司编号")
    @ExcelProperty(value = "供应商编号")
    private String companyCode;


    /**
     * 特殊发票标记 0-默认  1-通行费   2-成品油
     */
    @ApiModelProperty("special_invoice_flag")
    private Integer specialInvoiceFlag;


    @ApiModelProperty("扣款时间")
    private Date paymentTime;



}
