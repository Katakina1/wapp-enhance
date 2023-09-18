package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 例外报告代码枚举
 */
public enum VerifyStatusExportEnum {
    /**
     * 未匹配到蓝票
     */
    VERIFY_UNDO("0", "未验真"),
    VERIFY_FAIL("1", "验真失败"),
    VERIFY_SUCCESS("2", "验真成功");
    @Getter
    private final String code;
    @Getter
    private final String description;


    VerifyStatusExportEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static  String getValue(String code){
        for(VerifyStatusExportEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDescription();
            }
        }
        return null;
    }

    public static  String getCode(String value){
        for(VerifyStatusExportEnum ele:values()){
            if(ele.getDescription().equals(value)){
                return ele.getCode();
            }
        }
        return null;
    }


}
