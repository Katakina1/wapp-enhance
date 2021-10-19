package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:25
 **/
@Data
@ApiModel
public class InvoiceMatchListRequest {

    /**
     * 开票时间 开始
     */
    @ApiModelProperty("开票日期--开始")
    private String invoiceDateStart;
    /**
     * 开票时间 结束
     */
    @ApiModelProperty("开票日期--结束")
    private String invoiceDateEnd;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称-仅支持左匹配")
    private String goodsName;

//    /**
//     * 税率
//     */
//    @ApiModelProperty("税率,小数形式")
//    private Double taxRate;

    /**
     * 协议单号/EPD单号
     */
    @ApiModelProperty("协议单/EPD单号")
    private String billNo;

    /**
     * 协议单号/EPD单ID
     */
    @ApiModelProperty("协议单/EPD单ID")
    private Long billId;

}
