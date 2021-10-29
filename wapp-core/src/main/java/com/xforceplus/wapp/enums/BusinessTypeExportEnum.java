package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 例外报告代码枚举
 */
public enum BusinessTypeExportEnum {
    /**
     * 未匹配到蓝票
     */
    BUSINESS_TYPE_WATER("0", "水电费"),
    BUSINESS_TYPE_LEASING("1", "leasing in "),
    BUSINESS_TYPE_TRANFER("2", "固定资产转移");
    @Getter
    private final String code;
    @Getter
    private final String description;


    BusinessTypeExportEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static  String getValue(String code){
        for(BusinessTypeExportEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDescription();
            }
        }
        return null;
    }


}
