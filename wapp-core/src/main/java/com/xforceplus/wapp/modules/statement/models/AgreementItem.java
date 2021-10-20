package com.xforceplus.wapp.modules.statement.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("协议明细信息")
public class AgreementItem extends BaseInformation {
    @ApiModelProperty("唯一id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("明细编号")
    private String salesbillItemNo;

    @ApiModelProperty("明细代码")
    private String itemCode;

    @ApiModelProperty("明细名称")
    private String itemName;

    @ApiModelProperty("税编简称")
    private String itemShortName;

    @ApiModelProperty("规格型号")
    private String itemSpec;

    @ApiModelProperty("含税单价")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal unitPriceWithTax;

    @ApiModelProperty("单价")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal unitPrice;

    @ApiModelProperty("数量")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal quantity;

    @ApiModelProperty("单位")
    private String quantityUnit;

    @ApiModelProperty("含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithTax;

    @ApiModelProperty("不含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithoutTax;

    @ApiModelProperty("税额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxAmount;

    @ApiModelProperty("税率")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxRate;

    @ApiModelProperty("是否享受税收优惠政策 0 - 不 1- 是")
    private String taxPre;

    @ApiModelProperty("优惠政策内容")
    private String taxPreCon;

    @ApiModelProperty("零税率标志 空.非0税率、0.出口退税、1.免税、2.不征税、3.普通0税率")
    private String zeroTax;

    @ApiModelProperty("税收分类编码")
    private String goodsTaxNo;

    @ApiModelProperty("编码版本号")
    private String goodsNoVer;
}
