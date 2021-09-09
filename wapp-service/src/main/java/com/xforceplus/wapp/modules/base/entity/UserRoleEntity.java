package com.xforceplus.wapp.modules.base.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Daily.zhang on 2018/04/17.
 */
@Getter
@Setter
public class UserRoleEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户所在分库名
     */
    private String schemaLabel;

    /**
     * 用户角色id
     */
    private Integer userroleid;

    /**
     * 用户id
     */
    private Integer userid;

    /**
     * 角色id
     */
    private Integer roleid;

    private Long[] ids;

    public Long[] getIds() {
        return (ids == null) ? null : Arrays.copyOf(ids, ids.length);
    }

    public void setIds(Long[] ids) {
        this.ids = ids == null ? null : Arrays.copyOf(ids, ids.length);
    }
}
