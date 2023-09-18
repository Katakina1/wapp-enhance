package com.xforceplus.wapp.enums.customs;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;
import org.apache.ibatis.annotations.Param;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 发票勾选状态
 */
public enum InvoiceCheckEnum implements ValueEnum<Integer> {

    /**
     * 勾选状态 -1 - 撤销勾选失败 0-撤销勾选中 1-不可勾选  2-未勾选 3-勾选中 4-已勾选 5-勾选失败  6-抵扣异常 8-已确认抵扣 9-撤销勾选成功(属地使用)
     *
     */
    CHECK_N1(-1, "撤销勾选失败"),
    CHECK_0(0, "撤销勾选中"),
    CHECK_1(1,"不可勾选"),
    CHECK_2(2,"未勾选"),
    CHECK_3(3,"勾选中"),
    CHECK_4(4,"已勾选"),
    CHECK_5(5,"勾选失败"),
    CHECK_6(6,"抵扣异常"),
    CHECK_8(8,"已确认抵扣"),
    CHECK_9(9,"撤销勾选成功"),
     ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    InvoiceCheckEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static InvoiceCheckEnum getInvoiceCheckEnum(int code) {
        for (InvoiceCheckEnum value : InvoiceCheckEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }


    public static  String getValue(Integer code){
        for(InvoiceCheckEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDesc();
            }
        }
        return null;
    }


    public static Integer getValueDesc(String desc) {
        for (InvoiceCheckEnum ele : values()) {
            if (ele.getDesc().equals(desc)) {
                return ele.getCode();
            }
        }
        return null;
    }

    /**
     * 提供RMS不允许入账的海关票勾选状态
     * 费用扫描处理->确认
     * @return
     */
    public static EnumSet<InvoiceCheckEnum> noEntryAccount() {
        return EnumSet.of(
                InvoiceCheckEnum.CHECK_3,
                InvoiceCheckEnum.CHECK_4,
                InvoiceCheckEnum.CHECK_0,
                InvoiceCheckEnum.CHECK_N1,
                InvoiceCheckEnum.CHECK_8,
                InvoiceCheckEnum.CHECK_6);
    }

    /**
     * 提供RMS允许入账的海关票勾选状态
     * @return
     */
    public static EnumSet<InvoiceCheckEnum> doEntryAccount() {
        return EnumSet.of(
                InvoiceCheckEnum.CHECK_2,
                InvoiceCheckEnum.CHECK_5,
                InvoiceCheckEnum.CHECK_9);
    }

    public static EnumSet<InvoiceCheckEnum> isChecks() {
        return EnumSet.of(
                InvoiceCheckEnum.CHECK_4,
                InvoiceCheckEnum.CHECK_6,
                InvoiceCheckEnum.CHECK_8);
    }

}
