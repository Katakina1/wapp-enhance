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

    public Integer getRoleid() {
		return roleid;
	}

	public void setRoleid(Integer roleid) {
		this.roleid = roleid;
	}

	public Integer getMenuid() {
		return menuid;
	}

	public void setMenuid(Integer menuid) {
		this.menuid = menuid;
	}

	public List<Long> getMenuIdList() {
		return menuIdList;
	}

	public void setMenuIdList(List<Long> menuIdList) {
		this.menuIdList = menuIdList;
	}

	public List<Long> getRoleIdList() {
		return roleIdList;
	}

	public void setRoleIdList(List<Long> roleIdList) {
		this.roleIdList = roleIdList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setRolerightid(Integer rolerightid) {
		this.rolerightid = rolerightid;
	}

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
