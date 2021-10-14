package com.xforceplus.wapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mashaopeng@xforceplus.com
 */
@Getter
@AllArgsConstructor
public enum DefaultSettingEnum {
    /**
     *
     */
    OVERDUE_DEFAULT_DAY("OVERDUE_DEFAULT_DAY", "默认超期时间配置")
    ;
    public final String code;
    public final String message;
}
