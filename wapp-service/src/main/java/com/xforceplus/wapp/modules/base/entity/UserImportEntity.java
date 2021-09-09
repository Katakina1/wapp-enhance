package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 导入页面实体
 * @author Adil.Xu
 * @date 11/21/2018
 */
public class UserImportEntity extends AbstractBaseDomain {


    private static final long serialVersionUID = -8965622625473386843L;

    /**
     * excel导入的序号
     */
    private int indexNo;
    /**
     * 供应商号
     */
    @NotBlank
    @Length(max = 6)
    private String usercode;
    /**
     * 供应商名称
     */
    @NotBlank
    private String username;

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(int indexNo) {
        this.indexNo = indexNo;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
