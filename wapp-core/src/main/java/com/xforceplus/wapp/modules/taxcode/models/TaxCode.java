package com.xforceplus.wapp.modules.taxcode.models;

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
public class TaxCode {
    /**
     * 主键
     */
    private Long id;

    /**
     * 税编转换代码
     */
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

    /**
     * 扩展字段1,所属分类，门店号
     */
    private String ext1;

    /**
     * 扩展字段2
     */
    private String ext2;

    /**
     * 扩展字段3
     */
    private String ext3;

    /**
     * 扩展字段4
     */
    private String ext4;

    /**
     * 扩展字段5，税号
     */
    private String ext5;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}