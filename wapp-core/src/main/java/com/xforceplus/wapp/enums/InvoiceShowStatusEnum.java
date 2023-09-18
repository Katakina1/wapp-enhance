package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * Describe: 结算单 正式发票显示状态
 *
 * @Author xiezhongyong
 * @Date 2022/10/20
 */
public enum InvoiceShowStatusEnum {
    NORMAL_RED("0", "正常红票"),
    NORMAL_BLUE("1", "正常蓝票"),
    BLUE_OFFSET_AUTID("2", "蓝冲待审核"),
    RED_WAIT_OFFSET("3", "红票待蓝冲"),
    BLUE_AUTID("4", "蓝票待审核"),
    RED_BLUE_OFFSET("5", "红票已蓝冲"),
    ABNORMAL("6", "异常");

    @Getter
    private final String code;
    @Getter
    private final String description;


    InvoiceShowStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getValue(String code) {
        for (InvoiceShowStatusEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getDescription();
            }
        }
        return null;
    }

    public static String getCode(String val) {
        for (InvoiceShowStatusEnum ele : values()) {
            if (ele.getDescription().equals(val)) {
                return ele.getCode();
            }
        }
        return null;
    }


}
