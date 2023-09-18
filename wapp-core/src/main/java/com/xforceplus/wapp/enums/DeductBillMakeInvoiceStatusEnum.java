package com.xforceplus.wapp.enums;


import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Describe: 业务单开票状态(0:未开票;1:部分开票;2:已开票)
 *
 * @Author xiezhongyong
 * @Date 2022/9/23
 */
public enum DeductBillMakeInvoiceStatusEnum  {


    NONE_MAKE_INVOICE(0, "未开票"),
    PART_MAKE_INVOICE(1, "部分开票"),
    COMPLETE_MAKE_INVOICE(2, "已开票"),
    WAIT_MAKE_INVOICE(3, "待开票"),

    ;

    DeductBillMakeInvoiceStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;
    private String message;

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }

    public static DeductBillMakeInvoiceStatusEnum fromCode(String code) {
        return Arrays.stream(values()).filter(s -> s.code().equals(code))
                .findAny().orElse(null);
    }

    public static Collection<DeductBillMakeInvoiceStatusEnum> otherCode(DeductBillMakeInvoiceStatusEnum statusEnum){
        return Arrays.stream(values()).filter(x->x!=statusEnum).collect(Collectors.toList());
    }

}
