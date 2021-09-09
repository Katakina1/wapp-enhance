package com.xforceplus.wapp.modules.constantenum;

/**
 * 企业黑名单excel导入枚举类
 * Created by vito.xing on 2018/4/14
 */
public enum BlackEnterpriseEnum {
    TAXNO(1),ORGNAME(2),COMPANYTYPE(3);

    private int value;

    BlackEnterpriseEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
