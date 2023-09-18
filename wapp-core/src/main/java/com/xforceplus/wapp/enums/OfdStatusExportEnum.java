package com.xforceplus.wapp.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 例外报告代码枚举
 */
public enum OfdStatusExportEnum {
    /**
     * 未匹配到蓝票
     */
    SIGIN_UNDO("0", "未验签"),
    SIGIN_FAIL("1", "验签失败"),
    SIGIN_SUCCESS("2", "验签成功");
    @Getter
    private final String code;
    @Getter
    private final String description;


    OfdStatusExportEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static  String getValue(String code){
        for(OfdStatusExportEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDescription();
            }
        }
        return null;
    }
    public static  String getCode(String val){
        for(OfdStatusExportEnum ele:values()){
            if(ele.getDescription().equals(val)){
                return ele.getCode();
            }
        }
        return null;
    }

}
