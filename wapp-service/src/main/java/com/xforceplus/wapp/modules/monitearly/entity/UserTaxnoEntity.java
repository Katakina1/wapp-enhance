package com.xforceplus.wapp.modules.monitearly.entity;

import lombok.Getter;
import lombok.Setter;
import static com.google.common.base.MoreObjects.toStringHelper;

import java.io.Serializable;

/**
 * 用户企业表
 * Created by alfred.zong on 2018/04/12.
 */
@Setter
@Getter
public final class UserTaxnoEntity implements Serializable {

    private static final long serialVersionUID = -123960104394466589L;

    // 表ID
    private String id;

    // 用户ID
    private Long userid;

    //机构ID
    private String orgid;

    //分库所需要的参数
    private String schemaLabel;

    @Override
    public String toString() {
        return toStringHelper(this).
                add("id", id).
                add("userid", userid).
                add("orgid", orgid).
                add("schemaLabel", schemaLabel).
                toString();
    }

    private String loginname;

    private String username;
    //认证归属期
    private String rzhDate;
}
