package com.xforceplus.wapp.modules.statement.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@ApiModel("索赔明细")
public class ClaimItem {
    @ApiModelProperty("唯一ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("定案日期")
    private Long verdictDate;

    @ApiModelProperty("门店编码")
    private String purchaserNo;

    @ApiModelProperty("部门编码")
    private String deptNbr;

    @ApiModelProperty("供应商编码")
    private String sellerNo;

    @ApiModelProperty("中文品名")
    private String cnDesc;

    @ApiModelProperty("商品编码")
    private String itemNo;

    @ApiModelProperty("商品条码")
    private String upc;

    @ApiModelProperty("单价")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("税率")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxRate;

    @ApiModelProperty("项目数量")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal quantity;

    @ApiModelProperty("vnpk成本")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal vnpkCost;

    @ApiModelProperty("vnpk数量")
    private Integer vnpkQuantity;

    @ApiModelProperty("类别编码")
    private String gategoryNbr;

    @ApiModelProperty("剩余额度")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal remainingAmount;

    @ApiModelProperty("税收分类编码")
    private String goodsTaxNo;

    @ApiModelProperty("是否享受税收优惠政策 0 否 1 是")
    private String taxPre;

    @ApiModelProperty("优惠政策内容")
    private String taxPreCon;

    @ApiModelProperty("零税率")
    private String zeroTax;

    @ApiModelProperty("税编简称")
    private String itemShortName;

    @ApiModelProperty("税编版本")
    private String goodsNoVer;

    @ApiModelProperty("不含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithoutTax;

    @ApiModelProperty("含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithTax;

    @ApiModelProperty("更新时间")
    private Long updateDate;
}
