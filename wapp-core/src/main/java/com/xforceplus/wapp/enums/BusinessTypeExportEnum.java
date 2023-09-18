package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 例外报告代码枚举
 */
public enum BusinessTypeExportEnum {
    BUSINESS_TYPE_WATER("0", "水电费"),
    BUSINESS_TYPE_LEASING("1", "leasing in "),
    BUSINESS_TYPE_TRANFER("2", "固定资产转移"),
    BUSINESS_TYPE_CHC("3", "JV开给CHC"),
    BUSINESS_TYPE_MTR("4", "MTR本地订单-山姆"),
    BUSINESS_TYPE_INTERCOM("5", "Intercom-特殊收入分配"),
    BUSINESS_TYPE_ECOM("6", "E-Com"),
    BUSINESS_TYPE_BR("7", "BR-银行对账"),
    BUSINESS_TYPE_SR("8", "SR-门店对账"),
    BUSINESS_TYPE_HYPER("9", "MTR-Hyper"),
    BUSINESS_TYPE_GNFR("10", "GNFR"),
    BUSINESS_TYPE_THQ("11", "提货券"),
    ;
    @Getter
    private final String code;
    @Getter
    private final String description;


    BusinessTypeExportEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getValue(String code) {
        for (BusinessTypeExportEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getDescription();
            }
        }
        return null;
    }

    public static String getCode(String value) {
        for (BusinessTypeExportEnum ele : values()) {
            if (ele.getDescription().equals(value)) {
                return ele.getCode();
            }
        }
        return null;
    }


}
