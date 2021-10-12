package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="t_xf_overdue")
public class OverdueEntity {
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 供应商名称
     */
    private String sellerName;

    /**
     * 供应商税号
     */
    private String sellerTaxNo;

    /**
     * 超期时间
     */
    private Integer overdueDay;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建用户
     */
    private Long createUser;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 更新用户
     */
    private Long updateUser;

    /**
     * 删除标记
     */
    private String deleteFlag;
}
