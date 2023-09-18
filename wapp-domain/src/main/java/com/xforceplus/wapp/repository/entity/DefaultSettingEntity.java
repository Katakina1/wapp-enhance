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
@TableName(value="t_xf_default_setting")
public class DefaultSettingEntity {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 规则code
     */
    private String code;

    /**
     * 规则含义
     */
    private String meaning;

    /**
     * 规则值
     */
    private String value;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 更新用户
     */
    private String updateUser;
}
