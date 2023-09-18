package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName(value = "t_xf_tax_code_audit")
public class TaxCodeAuditEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String sellerNo;
    
    private String sellerName;

    /**
     * 税编转换代码
     */
    private String itemNo;

    /**
     * 商品和服务名称
     */
    private String itemName;

    private String before;

    private String after;

    private Integer auditStatus;

    private Date auditTime;

    private String auditOpinion;

    private Integer sendStatus;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新用户
     */
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}