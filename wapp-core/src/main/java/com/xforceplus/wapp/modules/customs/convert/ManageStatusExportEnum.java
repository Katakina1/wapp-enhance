package com.xforceplus.wapp.modules.customs.convert;

import lombok.Getter;

/**
 * 海关票状态
 */
public enum ManageStatusExportEnum {
    STATUS_0("0", "非正常"),
    STATUS_1("1", "正常");

    @Getter
    private final String code;
    @Getter
    private final String desc;


    ManageStatusExportEnum(String code, String description) {
        this.code = code;
        this.desc = description;
    }

    public static  String getValue(String code){
        for(ManageStatusExportEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }
    public static  String getValueByDesc(String code){
        for(ManageStatusExportEnum ele:values()){
            if(ele.getDesc().equals(code)){
                return ele.getCode();
            }
        }
        return null;
    }


}
