package com.xforceplus.wapp.enums.customs;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 缴款书来源
 */
public enum SourceTypeEnum implements ValueEnum<Integer> {

    /**
     * 缴款书来源1-缴款书采集、0-底账同步
     *
     */
    SOURCE_TYPE_0(0,"底账同步"),
    SOURCE_TYPE_1(1,"缴款书采集"),

     ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    SourceTypeEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static SourceTypeEnum getSourceTypeEnum(int code) {
        for (SourceTypeEnum value : SourceTypeEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }


    public static Integer getValue(String desc) {
        for (SourceTypeEnum ele : values()) {
            if (ele.getDesc().equals(desc)) {
                return ele.getCode();
            }
        }
        return null;
    }
}
