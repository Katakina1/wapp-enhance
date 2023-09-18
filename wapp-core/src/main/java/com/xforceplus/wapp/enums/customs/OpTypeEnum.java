package com.xforceplus.wapp.enums.customs;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 操作类型
 */
public enum OpTypeEnum implements ValueEnum<Integer> {

    /**
     * 操作类型1修改税款金额2修改所属期3修改凭证号4手工录入
     *
     */
    OP_TYPE_1(1,"修改税款金额"),
    OP_TYPE_2(2,"修改所属期"),
    OP_TYPE_3(3,"修改凭证号"),
    OP_TYPE_4(4,"手工录入"),
     ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    OpTypeEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static OpTypeEnum getOpTypeEnum(int code) {
        for (OpTypeEnum value : OpTypeEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }


    public static Integer getValue(String desc) {
        for (OpTypeEnum ele : values()) {
            if (ele.getDesc().equals(desc)) {
                return ele.getCode();
            }
        }
        return null;
    }
}
