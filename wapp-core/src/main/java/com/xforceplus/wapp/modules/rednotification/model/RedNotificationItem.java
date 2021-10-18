package com.xforceplus.wapp.modules.rednotification.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("红字信息表明细信息")
public class RedNotificationItem {
    @ApiModelProperty("订单明细号")
    private String detailNo;

    @ApiModelProperty("劳务货物名称")
    private String goodsName;

    @ApiModelProperty("税收分类编码")
    private String goodsTaxNo;

    /**
     * 是否享受优惠政策 1
     */
    @ApiModelProperty("是否享受优惠政策 1享受 0 不享受")
    private Integer taxPre;

    /**
     * 优惠内容
     */
    @ApiModelProperty("优惠内容")
    private String taxPreCon;

    /**
     * 零税率标识
     */
    @ApiModelProperty("零税率标识 零税率标志空-非0税率；0-出口退税1-免税2-不征税3-普通0税率")
    private Integer zeroTax;


    /**
     * 规格型号
     */
    @ApiModelProperty("规格型号")
    private String model;

    /**
     * 单位
     */
    @ApiModelProperty("unit")
    private String unit;

    /**
     * 数量
     */
    @ApiModelProperty("数量")
    private BigDecimal num;

    /**
     * 税率
     */
    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    /**
     * 单价
     */
    @ApiModelProperty("单价")
    private BigDecimal unitPrice;

    @ApiModelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    @ApiModelProperty("含税金额")
    private BigDecimal amountWithTax;

    @ApiModelProperty("税额")
    private BigDecimal taxAmount;

    /**
     * 税编转换代码
     */
    @ApiModelProperty("税编转换代码")
    private String taxConvertCode;

    @ApiModelProperty("明细主键")
    private Long id;
}
