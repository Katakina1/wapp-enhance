package com.xforceplus.wapp.modules.customs.convert;

import lombok.Getter;

/**
 * 比对状态 -1-比对失败 0-未比对 1-比对成功
 */
public enum BillStatusEnum {

    BILL_STATUS_N1("-1", "比对失败"),
    BILL_STATUS_0("0", "未比对"),
    BILL_STATUS_1("1", "比对成功");

    @Getter
    private final String code;
    @Getter
    private final String desc;


    BillStatusEnum(String code, String description) {
        this.code = code;
        this.desc = description;
    }

    public static  String getValue(String code){
        for(BillStatusEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }
    public static  String getValueByDesc(String code){
        for(BillStatusEnum ele:values()){
            if(ele.getDesc().equals(code)){
                return ele.getCode();
            }
        }
        return null;
    }


}
