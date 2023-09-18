package com.xforceplus.wapp.modules.noneBusiness.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Getter
public class MtrIcInvoiceDetailDto implements Serializable {
    private final static long serialVersionUID = 1L;
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    @ApiModelProperty("发票号码")
    private String invoiceNo;
    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private String detailAmount;

    /**
     * 税率
     */
    @ApiModelProperty("税率")
    private String taxRate;

    /**
     * 税额
     */
    @ApiModelProperty("税额")
    private String taxAmount;

    /**
     * 单位
     */
    @ApiModelProperty("单位")
    private String unit;

    /**
     * 数量
     */
    @ApiModelProperty("数量")
    private String num;

    /**
     * 单价
     */

    @ApiModelProperty("单价")
    private String unitPrice;

    /**
     * 规格型号
     */
    @ApiModelProperty("规格型号")
    private String model;

    /**
     * 货物或应税劳务名称
     */
    @ApiModelProperty("货物或应税劳务名称")
    private String goodsName;

}