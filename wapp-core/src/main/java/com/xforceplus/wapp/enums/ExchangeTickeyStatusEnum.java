package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 换票状态枚举值
 */
public enum ExchangeTickeyStatusEnum {
    EXCHANGE_TICKEY_STATUS_SH("0", "待审核"),
    EXCHANGE_TICKEY_STATUS_PRE("1", "待换票"),
    EXCHANGE_TICKEY_STATUS_UPLOAD("2", "已上传"),
    EXCHANGE_TICKEY_STATUS_DONE("3", "已完成"),
    EXCHANGE_TICKEY_STATUS_FAIL("4", "换票失败");
    @Getter
    private final String code;
    @Getter
    private final String description;


    ExchangeTickeyStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getValue(String code) {
        for (ExchangeTickeyStatusEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getDescription();
            }
        }
        return null;
    }


}
