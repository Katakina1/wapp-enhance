package com.xforceplus.wapp.modules.deduct.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by SunShiyong on 2021/10/21.
 */
@ApiModel("业务单明细对象")
@Data
public class DeductBillItemModel {
    @ApiModelProperty("业务单明细id")
    private Long id;

    @ApiModelProperty("商品编码")
    private String itemNo;

    @ApiModelProperty("商品名称")
    private String cnDesc;

    @ApiModelProperty("税收分类编码")
    private String goodsTaxNo;

    @ApiModelProperty("数量")
    private BigDecimal quantity;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    @ApiModelProperty("税额")
    private BigDecimal taxAmount;

    @ApiModelProperty("单位")
    private String unit;


}
