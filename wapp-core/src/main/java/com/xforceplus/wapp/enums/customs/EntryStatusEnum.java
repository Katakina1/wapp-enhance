package com.xforceplus.wapp.enums.customs;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 入账状态
 */
public enum EntryStatusEnum implements ValueEnum<String> {

    /**
     *  02,"入账企业所得税税前扣除",03,"入账企业所得税不扣除",06,"入账撤销失败"
     */
    ACCOUNT_02("02","入账企业所得税税前扣除"),
    ACCOUNT_03("03","入账企业所得税不扣除"),
    ACCOUNT_06("06","入账撤销"),
     ;

    @Getter
    private String code;
    @Getter
    private String desc;

    EntryStatusEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return code;
    }

    public static EntryStatusEnum getEntryStatusEnum(String code) {
        for (EntryStatusEnum value : EntryStatusEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }


    public static String getValue(String desc) {
        for (EntryStatusEnum ele : values()) {
            if (ele.getDesc().equals(desc)) {
                return ele.getCode();
            }
        }
        return null;
    }
}
