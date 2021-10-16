package com.xforceplus.wapp.modules.taxcode.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("税编信息")
public class TaxCode {
    @ApiModelProperty("唯一id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("税编转换代码")
    private String itemNo;

    @ApiModelProperty("税收分类编码")
    private String goodsTaxNo;

    @ApiModelProperty("税局货物名称")
    private String standardItemName;

    @ApiModelProperty("货物及服务代码")
    private String itemCode;

    @ApiModelProperty("商品和服务名称")
    private String itemName;

    @ApiModelProperty("是否享受税收优惠政策0-不1-享受")
    private String taxPre;

    @ApiModelProperty("享受税收优惠政策内容")
    private String taxPreCon;

    @ApiModelProperty("零税率标志空-非0税率；0-出口退税1-免税2-不征税3-普通0税率")
    private String zeroTax;

    @ApiModelProperty("大类名称")
    private String largeCategoryName;

    @ApiModelProperty("大类编码")
    private String largeCategoryCode;

    @ApiModelProperty("中类名称")
    private String medianCategoryName;

    @ApiModelProperty("中类编码")
    private String medianCategoryCode;

    @ApiModelProperty("小类名称")
    private String smallCategoryName;

    @ApiModelProperty("小类编码")
    private String smallCategoryCode;

    @ApiModelProperty("0:待处理 1:待确认 2:已生效")
    private String status;

    @ApiModelProperty("税率")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxRate;

    @ApiModelProperty("规格型号")
    private String itemSpec;

    @ApiModelProperty("单位")
    private String quantityUnit;

    @ApiModelProperty("更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long updateTime;

    private static final long serialVersionUID = 1L;
}