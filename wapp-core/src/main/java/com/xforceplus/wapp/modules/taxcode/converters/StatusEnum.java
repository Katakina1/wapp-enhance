package com.xforceplus.wapp.modules.taxcode.converters;

import lombok.Getter;

/**
 * 比对状态 -1-比对失败 0-未比对 1-比对成功
 */
public enum StatusEnum {

    STATUS_N1("-1", "上传失败"),
    STATUS_0("0", "默认"),
    STATUS_1("1", "比对一致"),
    STATUS_2("2", "比对不一致"),
    STATUS_3("3", "上传3.0平台成功"),
    STATUS_4("4", "上传已经存在"),
    STATUS_5("5", "未同步");

    @Getter
    private final String code;
    @Getter
    private final String desc;


    StatusEnum(String code, String description) {
        this.code = code;
        this.desc = description;
    }

    public static  String getValue(String code){
        for(StatusEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }
    public static  String getValueByDesc(String code){
        for(StatusEnum ele:values()){
            if(ele.getDesc().equals(code)){
                return ele.getCode();
            }
        }
        return null;
    }


}
