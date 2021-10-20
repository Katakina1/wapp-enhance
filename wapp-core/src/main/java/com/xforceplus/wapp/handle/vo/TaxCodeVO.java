package com.xforceplus.wapp.handle.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class TaxCodeVO {
    /**
     * 税编转换代码
     */
    @JsonProperty("taxConvertCode")
    @JSONField(name = "taxConvertCode")
    private String itemNo;

    /**
     * 税收分类编码
     */
    private String goodsTaxNo;

    /**
     * 税局货物名称
     */
    private String standardItemName;

    /**
     * 货物及服务代码
     */
    private String itemCode;

    /**
     * 商品和服务名称
     */
    private String itemName;

    /**
     * 是否享受税收优惠政策0-不1-享受
     */
    private String taxPre;

    /**
     * 享受税收优惠政策内容
     */
    private String taxPreCon;

    /**
     * 零税率标志空-非0税率；0-出口退税1-免税2-不征税3-普通0税率
     */
    private String zeroTax;

    /**
     * 大类名称
     */
    private String largeCategoryName;

    /**
     * 大类编码
     */
    private String largeCategoryCode;

    /**
     * 中类名称
     */
    private String medianCategoryName;

    /**
     * 中类编码
     */
    private String medianCategoryCode;

    /**
     * 小类名称
     */
    private String smallCategoryName;

    /**
     * 小类编码
     */
    private String smallCategoryCode;

    /**
     * 0:待处理 1:待确认 2:已生效
     */
    private String status;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 规格型号
     */
    private String itemSpec;

    /**
     * 单位
     */
    private String quantityUnit;
}
