package com.xforceplus.wapp.modules.base.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/16.
 */
@Getter
@Setter
public class RoleMenuEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色权限id
     */
    private Integer rolerightid;

    /**
     * 角色id
     */
    private Integer roleid;

    /**
     * 菜单id
     */
    private Integer menuid;

    private List<Long> menuIdList;

    private List<Long> roleIdList;

    public Integer getRolerightid() {
        return rolerightid;
    }
}
