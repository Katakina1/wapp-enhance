package com.xforceplus.wapp.enums;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author pengtao
**/
@Getter
public enum SuperServiceTypeEnum implements ValueEnum<Integer> {
    /**
     *    0.普通、1.VIP
     */
    NORMAL(0,"普通"),
    VIP(1,"VIP"),
    ;
    private final Integer code;
    private final String desc;

    SuperServiceTypeEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static SuperServiceTypeEnum fromValue(Integer code) {
        return Arrays.stream(SuperServiceTypeEnum.values()).filter(SuperServiceTypeEnum -> SuperServiceTypeEnum.code.equals(code)).findFirst().orElse(null);
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static Integer getValue(String desc) {
        for (SuperServiceTypeEnum ele : values()) {
            if (ele.getDesc().equals(desc)) {
                return ele.getCode();
            }
        }
        return null;
    }
}
