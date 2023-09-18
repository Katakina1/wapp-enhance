package com.xforceplus.wapp.modules.taxcode.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class RiversandtTcDto {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("商品号")
    private String itemNo;

    @ApiModelProperty("商品描述")
    private String itemName;

    @ApiModelProperty("商品税收编码")
    private String goodsTaxNo;

    @ApiModelProperty("销项税")
    private String taxRate;

    @ApiModelProperty("零税率标志")
    private String zeroTax;

    @ApiModelProperty("优惠政策标识")
    private String taxPre;

    @ApiModelProperty("优惠政策内容")
    private String taxPreCon;

    @ApiModelProperty("同步状态")
    private String status;

    private Date updateTime;

    private Date createTime;
}
