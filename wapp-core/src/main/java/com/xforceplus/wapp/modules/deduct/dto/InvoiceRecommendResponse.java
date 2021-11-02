package com.xforceplus.wapp.modules.deduct.dto;

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
public class InvoiceRecommendResponse {
    private String invoiceDate;
    private String invoiceNo;
    private String invoiceCode;
    private String goodsName;
    /**
     * 可用金额
     */
    @ApiModelProperty("匹配金额")
    private BigDecimal remainingAmount;

    @ApiModelProperty("发票ID")
    private Long id;
}
