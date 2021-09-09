package com.xforceplus.wapp.modules.signin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 文件实体
 * @author Colin.hu
 * @date 5/9/2018
 */
@Getter @Setter @ToString
public final class ExportEntity implements Serializable {

    private static final long serialVersionUID = 2540655405487516984L;

    /**
     * 人员id
     */
    private Long userId;

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSchemaLabel() {
		return schemaLabel;
	}

	public void setSchemaLabel(String schemaLabel) {
		this.schemaLabel = schemaLabel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 帐号
     */
    private String userAccount;

    /**
     * 人名
     */
    private String userName;

    /**
     * 分库标识
     */
    private String schemaLabel;

}
