package com.xforceplus.wapp.modules.taxcode.converters;

import lombok.Getter;

/**
 * 同步状态  0-未同步 1-已同步
 */
public enum SyncStatusEnum {

    STATUS_0("0", "未同步"),
    STATUS_1("1", "已同步");

    @Getter
    private final String code;
    @Getter
    private final String desc;


    SyncStatusEnum(String code, String description) {
        this.code = code;
        this.desc = description;
    }

    public static  String getValue(String code){
        for(SyncStatusEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }
    public static  String getValueByDesc(String code){
        for(SyncStatusEnum ele:values()){
            if(ele.getDesc().equals(code)){
                return ele.getCode();
            }
        }
        return null;
    }


}
