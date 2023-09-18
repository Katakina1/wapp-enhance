package com.xforceplus.wapp.modules.customs.convert;

import lombok.Getter;

/**
 * 勾选状态
 */
public enum CheckTypeExportEnum {
    CHECK_TYPE__1("-1", "撤销勾选失败"),
    CHECK_TYPE_0("0", "撤销勾选中"),
    CHECK_TYPE_1("1", "不可勾选"),
    CHECK_TYPE_2("2", "未勾选"),
    CHECK_TYPE_3("3", "勾选中"),
    CHECK_TYPE_4("4", "已勾选"),
    CHECK_TYPE_5("5", "勾选失败"),
    CHECK_TYPE_6("6", "抵扣异常"),
    CHECK_TYPE_8("8", "已确认抵扣"),
    CHECK_TYPE_9("9", "撤销勾选成功");
    @Getter
    private final String code;
    @Getter
    private final String desc;


    CheckTypeExportEnum(String code, String description) {
        this.code = code;
        this.desc = description;
    }

    public static  String getValue(String code){
        for(CheckTypeExportEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }
    public static  String getValueByDesc(String code){
        for(CheckTypeExportEnum ele:values()){
            if(ele.getDesc().equals(code)){
                return ele.getCode();
            }
        }
        return null;
    }


}
