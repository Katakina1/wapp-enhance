package com.xforceplus.wapp.modules.deduct.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 类描述：业务单占用蓝票明细信息表
 */
@Data
@ApiModel
public class DeductInvoiceDetailData {
    @ApiModelProperty(value = "发票ID")
    private Long invoiceId;
    @ApiModelProperty(value = "发票明细ID")
    private Long invoiceDetailId;

    @ApiModelProperty("匹配数量")
    private BigDecimal matchedNum;
    @ApiModelProperty("匹配单价")
    private BigDecimal matchedUnitPrice;
    @ApiModelProperty("匹配金额")
    private BigDecimal matchedDetailAmount;
    @ApiModelProperty("匹配税额")
    private BigDecimal matchedTaxAmount;
    @ApiModelProperty("剩余数量")
    private BigDecimal leftNum;
    @ApiModelProperty("剩余金额")
    private BigDecimal leftDetailAmount;

}
