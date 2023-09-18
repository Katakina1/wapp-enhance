package com.xforceplus.wapp.modules.customs.convert;

import lombok.Getter;

/**
 * 抵扣用途
 */
public enum CheckPurposeExportEnum {

    CHECK_PURPOSE_1("1", "抵扣勾选"),
    CHECK_PURPOSE_2("2", "不抵扣勾选"),
    CHECK_PURPOSE_10("10", "撤销抵扣勾选"),
    CHECK_PURPOSE_3("3", "退税勾选"),
    CHECK_PURPOSE_30("30", "退税撤销勾选");

    @Getter
    private final String code;
    @Getter
    private final String desc;


    CheckPurposeExportEnum(String code, String description) {
        this.code = code;
        this.desc = description;
    }

    public static  String getValue(String code){
        for(CheckPurposeExportEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }
    public static  String getValueByDesc(String code){
        for(CheckPurposeExportEnum ele:values()){
            if(ele.getDesc().equals(code)){
                return ele.getCode();
            }
        }
        return null;
    }


}
