package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * t_xf_taxcode_config
 * @author 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="t_xf_tax_code")
public class TaxCode {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 税编转换代码
     */
    private String taxConvertCode;

    /**
     * 租户代码
     */
    private String tenantCode;

    /**
     * 租户名称
     */
    private String tenantName;

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
     * 创建用户
     */
    private String createUserId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 更新用户
     */
    private String updateUserId;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 0:待处理 1:待确认 2:已生效
     */
    private String status;

    /**
     * 税率
     */
    private BigDecimal taxRate;

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
     * 规格型号
     */
    private String itemSpec;

    /**
     * 单位
     */
    private String quantityUnit;

    private static final long serialVersionUID = 1L;
}