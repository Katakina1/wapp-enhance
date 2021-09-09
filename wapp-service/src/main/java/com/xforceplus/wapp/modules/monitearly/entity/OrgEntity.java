package com.xforceplus.wapp.modules.monitearly.entity;

import lombok.Getter;
import lombok.Setter;
import static com.google.common.base.MoreObjects.toStringHelper;

import java.io.Serializable;

/**
 * 组织机构实体类
 * Created by alfred.zong on 2018/04/12.
 */
@Setter
@Getter
public final class OrgEntity implements Serializable {

    private static final long serialVersionUID = -578735539162852538L;

    //机构ID
    private int orgId;

    //纳税人识别号
    private String taxNo;

    //纳税人名称
    private String taxName;

    @Override
    public String toString() {
        return toStringHelper(this).
                add("orgId", orgId).
                add("taxNo", taxNo).
                add("taxName", taxName).
                toString();
    }
}
