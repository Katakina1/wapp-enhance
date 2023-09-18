package com.xforceplus.wapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by SunShiyong on 2021/10/21.
 * 业务单tab枚举
 */
@AllArgsConstructor
@Getter
public enum DeductBillTabEnum {

    TO_BE_MATCH("0","待匹配结算单"),
    MATCHED_TO_BE_MAKE("1","已匹配待开红字信息表"),
    APPLYED_RED_NO("2","已申请红字信息"),
    MAKEED("3","已开红票"),
    CANCELED("4","已取消"),
    NO_SPLIT_INVOICE("9","待拆票");

    private final String value;
    private final String desc;

}
