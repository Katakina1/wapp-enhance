package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_ac_org_discount_rate_log")
public class OrgLogEntity {
    /**
     * 机构ID
     */
    @TableField("orgid")
    private Long orgid;
    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 修改前折扣率
     */
    @TableField("discount_rate_before")
    private Long updateBefore;
    /**
     * 修改后折扣率
     */
    @TableField("discount_rate_after")
    private Long updateAfter;
    /**
     * 更改时间
     */
    @TableField("update_time")
    private String updateTime;
}
