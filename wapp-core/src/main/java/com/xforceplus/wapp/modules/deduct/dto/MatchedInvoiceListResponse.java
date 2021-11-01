package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:46
 **/
@Data
public class MatchedInvoiceListResponse {
    /**
     * 发票号码
     */
    @ApiModelProperty("发票号码")
    private String invoiceNo;
    /**
     * 发票代码
     */
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String goodsName;
    /**
     * 开票日期
     */
    @ApiModelProperty("开票日期")
    private String invoiceDate;
    /**
     * 可用金额
     */
    @ApiModelProperty("匹配金额")
    private BigDecimal matchedAmount;

    @ApiModelProperty("发票ID")
    private Long id;
}
