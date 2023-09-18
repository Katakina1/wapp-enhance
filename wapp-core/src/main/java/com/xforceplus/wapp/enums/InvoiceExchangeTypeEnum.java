package com.xforceplus.wapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by SunShiyong on 2021/11/19.
 */
@AllArgsConstructor
@Getter
public enum InvoiceExchangeTypeEnum {

    SP("1","商品"),
    FY("2","费用"),
    WBHP("3","外部红票"),
    NBHP("4","内部红票"),
    GDZC("5","固定资产"),
    ZL("6","租赁"),
    SPCG("0001","租赁"),
    ZJRZ("7","直接认证");


    private String code;
    private String desc;

    public static  String getValue(String code){
        for(InvoiceExchangeTypeEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }
}
