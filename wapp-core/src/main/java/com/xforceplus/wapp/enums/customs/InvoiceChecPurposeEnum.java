package com.xforceplus.wapp.enums.customs;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 发票勾选用途
 */
public enum InvoiceChecPurposeEnum implements ValueEnum<Integer> {

    /**
     * 抵扣用途1-抵扣勾选,2-不抵扣勾选,3-退税勾选,30-退税撤销勾选（确认后无法撤销）
     *
     */
    PURPOSE_1(1,"抵扣勾选"),
    PURPOSE_10(10,"撤销抵扣勾选"),
    PURPOSE_3(3,"退税勾选"),
    PURPOSE_30(30,"退税撤销勾选（确认后无法撤销）"),
     ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    InvoiceChecPurposeEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static InvoiceChecPurposeEnum getInvoiceCheckEnum(int code) {
        for (InvoiceChecPurposeEnum value : InvoiceChecPurposeEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }


    public static Integer getValue(String desc) {
        for (InvoiceChecPurposeEnum ele : values()) {
            if (ele.getDesc().equals(desc)) {
                return ele.getCode();
            }
        }
        return null;
    }
}
