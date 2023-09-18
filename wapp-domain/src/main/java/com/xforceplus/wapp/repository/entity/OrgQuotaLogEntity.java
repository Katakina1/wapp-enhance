package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_ac_org_quota_log")
public class OrgQuotaLogEntity {
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
     * 修改限额
     */
    @TableField("quota_before")
    private Long updateBefore;
    /**
     * 修改后限额
     */
    @TableField("quota_after")
    private Long updateAfter;
    /**
     * 更改时间
     */
    @TableField("update_time")
    private String updateTime;
}
