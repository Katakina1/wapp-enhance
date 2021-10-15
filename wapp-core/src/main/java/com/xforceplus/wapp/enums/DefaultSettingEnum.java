package com.xforceplus.wapp.enums;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mashaopeng@xforceplus.com
 */
@Getter
@AllArgsConstructor
public enum DefaultSettingEnum implements ValueEnum<String> {
    /**
     *
     */
    CLAIM_OVERDUE_DEFAULT_DAY("CLAIM_OVERDUE_DEFAULT_DAY", "默认超期时间配置(索赔)"),
    AGREEMENT_OVERDUE_DEFAULT_DAY("AGREEMENT_OVERDUE_DEFAULT_DAY", "默认超期时间配置(协议)"),
    EPD_OVERDUE_DEFAULT_DAY("EPD_OVERDUE_DEFAULT_DAY", "默认超期时间配置(EPD)")
    ;
    public final String value;
    public final String message;
}
