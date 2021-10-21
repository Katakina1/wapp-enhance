package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * updateGoodsTaxNo,updateLimitAmount,updateGoodsItem
 * 预制发票申请红字信息  操作类型  1 修改税编 2 修改限额 3 修改商品
 */
@Getter
@AllArgsConstructor
public enum ApplyOperationType implements ValueEnum<Integer>{
    updateGoodsTaxNo(1,"修改税编"),
    updateLimitAmount(2,"修改限额"),
    updateGoodsItem(3,"修改商品");

    private final Integer value;
    private final String desc;

}
