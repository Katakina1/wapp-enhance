package com.xforceplus.wapp.modules.constantenum;

/**
 * 商品税收分类excel导入枚举类
 * Created by vito.xing on 2018/4/16
 */
public enum GoodsTaxCodeEnum {
    GOODSCODE(1),GOODSNAME(2),TAXCODE(3),TAXCODENAME(4);

    private int value;

    GoodsTaxCodeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
