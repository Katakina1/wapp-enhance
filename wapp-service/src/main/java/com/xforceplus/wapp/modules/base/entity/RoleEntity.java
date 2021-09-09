package com.xforceplus.wapp.modules.base.entity;


import com.xforceplus.wapp.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色
 *
 * Created by Daily.zhang on 2018/04/13.
 */
@Getter
@Setter
public class RoleEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户所在分库名
     */
    private String schemaLabel;

    /**
     * 角色id
     */
    private Integer roleid;

    /**
     * 角色名称
     */
    private String rolename;

    /**
     * 角色编码
     */
    private String rolecode;

    /**
     * 所属机构
     */
    private Integer orgid;

    /**
     * 角色描述
     */
    private String roledesc;

    /**
     * 角色类型
     */
    private String roletype;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改时间
     */
    private Date lastModifyTime;

    /**
     * 修改人
     */
    private String lastModifyBy;

    public Date getCreateTime() {
        return DateUtils.obtainValidDate(this.createTime);
    }

    public void setCreateTime(Date createTime) {
        this.createTime = DateUtils.obtainValidDate(createTime);
    }

    public Date getLastModifyTime() {
        return DateUtils.obtainValidDate(this.lastModifyTime);
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = DateUtils.obtainValidDate(lastModifyTime);
    }
}
