package com.xforceplus.wapp.modules.taxcode.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxCodeDto {
    @JsonPropertyDescription("唯一id")
    private Long id;
    @JsonPropertyDescription("税编转换代码")
    private String itemNo;
    @JsonPropertyDescription("税收分类编码")
    private String goodsTaxNo;
    @JsonPropertyDescription("税局货物名称")
    private String standardItemName;
    @JsonPropertyDescription("货物及服务代码")
    private String itemCode;
    @JsonPropertyDescription("商品和服务名称")
    private String itemName;
    @JsonPropertyDescription("税编简称")
    private String itemShortName;
    @JsonPropertyDescription("是否享受税收优惠政策0-不1-享受")
    private String taxPre;
    @JsonPropertyDescription("享受税收优惠政策内容")
    private String taxPreCon;
    @JsonPropertyDescription("零税率标志空-非0税率；0-出口退税1-免税2-不征税3-普通0税率")
    private String zeroTax;
    @JsonPropertyDescription("大类名称")
    private String largeCategoryName;
    @JsonPropertyDescription("大类编码")
    private String largeCategoryCode;
    @JsonPropertyDescription("中类名称")
    private String medianCategoryName;
    @JsonPropertyDescription("中类编码")
    private String medianCategoryCode;
    @JsonPropertyDescription("小类名称")
    private String smallCategoryName;
    @JsonPropertyDescription("小类编码")
    private String smallCategoryCode;
    @JsonPropertyDescription("0:待处理 1:待确认 2:已生效")
    private String status;
    @JsonPropertyDescription("税率")
    private BigDecimal taxRate;
    @JsonPropertyDescription("规格型号")
    private String itemSpec;
    @JsonPropertyDescription("单位")
    private String quantityUnit;
    @JsonPropertyDescription("更新时间")
    private Long updateTime;
}