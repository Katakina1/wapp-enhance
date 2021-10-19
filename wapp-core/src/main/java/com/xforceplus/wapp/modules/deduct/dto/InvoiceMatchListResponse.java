package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:46
 **/
@Data
public class InvoiceMatchListResponse {
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
    @ApiModelProperty("可用金额")
    private String availableAmount;
    /**
     * 行序号
     */
    private Integer rownumber;
}
