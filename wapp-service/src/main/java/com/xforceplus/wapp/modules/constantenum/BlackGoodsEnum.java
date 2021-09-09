package com.xforceplus.wapp.modules.constantenum;

/**
 * 商品黑名单excel导入枚举类
 * Created by vito.xing on 2018/4/16
 */
public enum BlackGoodsEnum {
    GOODSCODE(1),GOODSNAME(2),REMARK(3);

    private int value;

    BlackGoodsEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
