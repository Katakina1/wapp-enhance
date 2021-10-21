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

    TO_BE_MATCH("0","待匹配蓝票"),
    MATCHED_TO_BE_MAKE("1","已匹配待开票"),
    APPLYED_RED_NO("2","已申请红字信息"),
    MAKEED("3","已开票"),
    CANCELED("4","已撤销");

    private final String value;
    private final String desc;

}
