package com.xforceplus.wapp.modules.base.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 账号中心请求实体
 *
 * Created by Daily.zhang on 2018/04/23.
 */
@Getter
@Setter
public class AccountCentRequest implements Serializable {

    private static final long serialVersionUID = 7198726088584970540L;
    /**
     * 用户账号
     */
    private String username;

    /**
     * 登录密码
     */
    private String pwd;

    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 用户邮箱
     */
    private String userMail;

    /**
     * 旧密码
     */
    private String oldPwd;

    /**
     * 新密码
     */
    private String newPwd;

    /**
     * 是否修改密码 0 否 1是
     */
    private String updatePwdType;

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
