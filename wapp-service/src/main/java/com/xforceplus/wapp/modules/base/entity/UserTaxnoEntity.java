package com.xforceplus.wapp.modules.base.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by Daily.zhang on 2018/04/19.
 */
@Getter
@Setter
public class UserTaxnoEntity extends BaseEntity  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 机构名称
     */
    private String orgname;

    /**
     * 纳税人识别号
     */
    private String taxno;

    /**
     * 所属中心企业
     */
    private String company;

    /**
     * 用户税号关联表id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userid;

    /**
     * 机构id
     */
    private Integer orgid;

    /**
     * 机构父级id
     */
    private Integer parentId;

    /**
     * 子级机构id
     */
    private String subOrgIdStr;
}
