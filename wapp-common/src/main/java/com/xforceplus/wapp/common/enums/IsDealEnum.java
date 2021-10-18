package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by SunShiyong on 2021/10/18.
 * 是否删除
 */
@Getter
@AllArgsConstructor
public enum  IsDealEnum {

    NO("0","否"),
    YES("1","是");

    private  final String value;
    private  final String desc;

}
