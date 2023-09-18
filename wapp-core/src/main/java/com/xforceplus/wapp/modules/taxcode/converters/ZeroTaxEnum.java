package com.xforceplus.wapp.modules.taxcode.converters;

import lombok.Getter;

/**
 * 零税率标识 空 - 非0税率，0-出口退税，1-免税，2-不征税，3-普通0税率
 */
public enum ZeroTaxEnum {
//    STATUS_N(null, "非0税率"),
    STATUS_E("", "非0税率"),
    STATUS_0("0", "出口退税"),
    STATUS_1("1", "免税"),
    STATUS_2("2", "不征税"),
    STATUS_3("3", "普通0税率");

    @Getter
    private final String code;
    @Getter
    private final String desc;


    ZeroTaxEnum(String code, String description) {
        this.code = code;
        this.desc = description;
    }

    public static  String getValue(String code){
        for(ZeroTaxEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }

    public static  String getValueByDesc(String code){
        for(ZeroTaxEnum ele:values()){
            if(ele.getDesc().equals(code)){
                return ele.getCode();
            }
        }
        return null;
    }


}
