package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;


public class RoleEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = -3322927968003764966L;
    private String roleCode;


    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
