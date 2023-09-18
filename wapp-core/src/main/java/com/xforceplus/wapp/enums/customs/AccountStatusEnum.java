package com.xforceplus.wapp.enums.customs;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 入账状态
 */
public enum AccountStatusEnum implements ValueEnum<String> {

    /**
     * 00, "未入账",01, "入账中",02,"入账企业所得税税前扣除",03,"入账企业所得税不扣除",04,"入账失败",05,"入账撤销中",06,"入账撤销",07,"入账撤销失败"
     */
    ACCOUNT_00("00", "未入账"),
    ACCOUNT_01("01", "入账中"),
    ACCOUNT_02("02", "入账企业所得税税前扣除"),
    ACCOUNT_03("03", "入账企业所得税不扣除"),
    ACCOUNT_04("04", "入账失败"),
    ACCOUNT_05("05", "入账撤销中"),
    ACCOUNT_06("06", "入账撤销"),
    ACCOUNT_07("07", "入账撤销失败"),
    ;

    @Getter
    private String code;
    @Getter
    private String desc;

    AccountStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return code;
    }

    public static AccountStatusEnum getAccountStatusEnum(String code) {
        for (AccountStatusEnum value : AccountStatusEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }

    public static  String getValue(String code){
        for(AccountStatusEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }

    public static String getValueDesc(String desc) {
        for (AccountStatusEnum ele : values()) {
            if (ele.getDesc().equals(desc)) {
                return ele.getCode();
            }
        }
        return null;
    }
}
