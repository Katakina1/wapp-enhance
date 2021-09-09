package com.xforceplus.wapp.modules.base.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 账号中心响应实体
 *
 * Created by Daily.zhang on 2018/04/23.
 */
@Getter
@Setter
public class AccountCentResponse implements Serializable {
    private static final long serialVersionUID = -9024571967485499857L;

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回描述
     */
    private String message;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 返回的用户信息
     * @return
     */
    private AccountCentUser user;

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
