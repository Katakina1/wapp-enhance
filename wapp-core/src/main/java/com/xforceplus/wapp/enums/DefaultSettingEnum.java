package com.xforceplus.wapp.enums;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mashaopeng@xforceplus.com
 */
@Getter
@AllArgsConstructor
public enum DefaultSettingEnum implements ValueEnum<Integer> {
    /**
     *
     */
    CLAIM_OVERDUE_DEFAULT_DAY(1, "CLAIM_OVERDUE_DEFAULT_DAY", "默认超期时间配置(索赔)"),
    AGREEMENT_OVERDUE_DEFAULT_DAY(2, "AGREEMENT_OVERDUE_DEFAULT_DAY", "默认超期时间配置(协议)"),
    EPD_OVERDUE_DEFAULT_DAY(3, "EPD_OVERDUE_DEFAULT_DAY", "默认超期时间配置(EPD)"),
    RED_INFORMATION_SWITCH(4, "RED_INFORMATION_SWITCH", "红字信息申请开关"),
    ;
    public final Integer value;
    public final String code;
    public final String message;
}
