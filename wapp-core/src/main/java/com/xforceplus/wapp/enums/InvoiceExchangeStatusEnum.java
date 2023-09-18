package com.xforceplus.wapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by SunShiyong on 2021/11/19.
 */
@AllArgsConstructor
@Getter
public enum InvoiceExchangeStatusEnum {

    DEFAULT(0,"初始"),
    TO_BE_EXCHANGE(1,"待换票"),
    UPLOADED(2,"已上传"),
    FINISHED(3,"已完成");


    private Integer code;
    private String desc;

    public static  String getValue(String code){
        for(InvoiceExchangeStatusEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }
}
