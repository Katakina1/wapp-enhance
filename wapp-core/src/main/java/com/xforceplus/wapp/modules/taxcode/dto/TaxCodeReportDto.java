package com.xforceplus.wapp.modules.taxcode.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-20 11:16
 **/
@Setter
@Getter
@ApiModel
public class TaxCodeReportDto {

    /**
     * 例外说明
     */
    @ApiModelProperty("例外说明")
    private String reportDesc;

    /**
     * 报告时间
     */
    @ApiModelProperty("报告时间")
    private String updateTime;

    /**
     * 商品号
     */
    @ApiModelProperty("商品号")
    private String itemNo;

    /**
     * 商品描述
     */
    @ApiModelProperty("商品描述")
    private String itemName;

    /**
     * 商品税收编码
     */
    @ApiModelProperty("商品税收编码")
    private String goodsTaxNo;

    /**
     * 销项税
     */
    @ApiModelProperty("销项税")
    private String taxRate;

    /**
     * 零税率标志
     */
    @ApiModelProperty("零税率标志")
    private String zeroTax;

    /**
     * 优惠政策标识
     */
    @ApiModelProperty("优惠政策标识")
    private String taxPre;

    /**
     * 优惠政策内容
     */
    @ApiModelProperty("优惠政策内容")
    private String taxPreCon;

}
