package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel
public class InvoiceRecommendDetail {
    @ApiModelProperty(value = "发票明细ID")
    private Long id;
    @ApiModelProperty(value = "唯一标识(发票代码+发票号码)")
    private String uuid;
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    @ApiModelProperty("发票号码")
    private String invoiceNo;
    @ApiModelProperty("明细序号")
    private String detailNo;
    @ApiModelProperty("货物或应税劳务名称")
    private String goodsName;
    @ApiModelProperty("商品编码")
    private String goodsNum;
    @ApiModelProperty("规格型号")
    private String model;
    @ApiModelProperty("单位")
    private String unit;
    @ApiModelProperty("数量")
    private String num;
    @ApiModelProperty("剩余数量")
    private String leftNum;
    @ApiModelProperty("单价")
    private String unitPrice;
    @ApiModelProperty("金额")
    private String detailAmount;
    @ApiModelProperty("剩余金额")
    private String leftDetailAmount;
    @ApiModelProperty("税率")
    private String taxRate;
    @ApiModelProperty("税额")
    private String taxAmount;

}
