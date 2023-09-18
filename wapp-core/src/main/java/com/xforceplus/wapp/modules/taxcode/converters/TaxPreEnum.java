package com.xforceplus.wapp.modules.taxcode.converters;

import lombok.Getter;

/**
 * 是否享受税收优惠政策 0-不享受1-享受
 */
public enum TaxPreEnum {
//    STATUS_N(null, "空"),
    STATUS_E("", "空"),
    STATUS_0("0", "不享受"),
    STATUS_1("1", "享受");

    @Getter
    private final String code;
    @Getter
    private final String desc;


    TaxPreEnum(String code, String description) {
        this.code = code;
        this.desc = description;
    }

    public static  String getValue(String code){
        for(TaxPreEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }
    public static  String getValueByDesc(String code){
        for(TaxPreEnum ele:values()){
            if(ele.getDesc().equals(code)){
                return ele.getCode();
            }
        }
        return null;
    }


}
