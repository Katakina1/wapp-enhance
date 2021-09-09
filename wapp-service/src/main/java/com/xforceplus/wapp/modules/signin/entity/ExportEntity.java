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
