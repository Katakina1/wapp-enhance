package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 大象慧云认证状态枚举
 */
public enum AuthStatusEnum {
    AUTH_STATUS_UN("0", "未认证"),
    AUTH_STATUST_CHECK("1", "已勾选未确认"),
    AUTH_STATUST_CONFIRM("2", "已确认"),
    AUTH_STATUS_SEND_CONFIRM("3", "已发送认证"),
    AUTH_STATUS_SUCCESS("4", "认证成功"),
    ANTU_STATUS_FAIL("5", "认证失败");
    @Getter
    private final String code;
    @Getter
    private final String description;


    AuthStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getValue(String code) {
        for (AuthStatusEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getDescription();
            }
        }
        return null;
    }


}
