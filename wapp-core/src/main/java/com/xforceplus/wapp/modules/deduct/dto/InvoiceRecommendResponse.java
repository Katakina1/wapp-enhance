package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-11-01 14:21
 **/
@Setter
@Getter
@ApiModel
public class InvoiceRecommendResponse {
    @ApiModelProperty("发票ID")
    private Long id;
    @ApiModelProperty("开票日期")
    private String invoiceDate;
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    @ApiModelProperty("发票号码")
    private String invoiceNo;
    @ApiModelProperty("商品名称")
    private String goodsName;
    @ApiModelProperty("是否成品油")
    private Boolean isOil;
    @ApiModelProperty("剩余匹配金额")
    private BigDecimal remainingAmount;
}
