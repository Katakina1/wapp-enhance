package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class MatchedInvoiceDetailBean {
    @ApiModelProperty("发票ID")
    private Long invoiceId;
    @ApiModelProperty("发票明细ID")
    private Long invoiceDetailId;
    @ApiModelProperty("业务单号")
    private String businessNo;
    @ApiModelProperty(value = "唯一标识(发票代码+发票号码)")
    private String uuid;
    @ApiModelProperty("发票号码")
    private String invoiceNo;
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    @ApiModelProperty("货物或应税劳务名称")
    private String goodsName;
    @ApiModelProperty("规格型号")
    private String model;
    @ApiModelProperty("开票日期")
    private String invoiceDate;
    @ApiModelProperty("商品编码")
    private String goodsNum;
    @ApiModelProperty("单位")
    private String unit;
    @ApiModelProperty("数量")
    private String num;
    @ApiModelProperty("单价")
    private String unitPrice;
    @ApiModelProperty("金额")
    private String detailAmount;
    @ApiModelProperty("税率")
    private String taxRate;
    @ApiModelProperty("税额")
    private String taxAmount;

    @ApiModelProperty("匹配数量")
    private String matchedNum;
    @ApiModelProperty("匹配单价")
    private String matchedUnitPrice;
    @ApiModelProperty("匹配金额")
    private String matchedDetailAmount;
    @ApiModelProperty("匹配税额")
    private String matchedTaxAmount;
    @ApiModelProperty("剩余数量")
    private String leftNum;
    @ApiModelProperty("剩余金额")
    private String leftDetailAmount;
}
