package com.xforceplus.wapp.enums;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 结算单状态
 */
public enum TXfSettlementStatusEnum implements ValueEnum<Integer> {

    /**
     * 待确认(1,8,9,10)、待开票(2)、部分开票(3)、已开票(4)、已完成(5)、待审核(6)、已撤销(7)、已删除(0页面不展示)
     */
    DELETED(0, "已删除"),
    WAIT_CONFIRM(1,"待确认"),
    NO_UPLOAD_RED_INVOICE(2,"待开票"),
    UPLOAD_HALF_RED_INVOICE(3,"已开部分票"),
    UPLOAD_RED_INVOICE(4,"已开票"),
    FINISH(5,"已完成"),
    WAIT_CHECK(6,"待审核"),
    DESTROY(7,"已撤销"),
    WAIT_MATCH_BLUE_INVOICE(8,"待匹配蓝票"),
    WAIT_SPLIT_INVOICE(9,"待拆票"),
    WAIT_MATCH_TAX_CODE(10,"待匹配税编"),
     ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    TXfSettlementStatusEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static TXfSettlementStatusEnum getTXfSettlementStatusEnum(int code) {
        for (TXfSettlementStatusEnum value : TXfSettlementStatusEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 可撤销状态
     * @param code 结算单状态
     * @return true-可撤销 false-不可撤销
     */
    public static boolean isCanDestroy(Integer code) {
        return isWaitConfirm(code)
                || NO_UPLOAD_RED_INVOICE.getCode().equals(code)
                || WAIT_CHECK.getCode().equals(code);
    }

    public static boolean isWaitConfirm(Integer code) {
        return WAIT_CONFIRM.getCode().equals(code)
                || WAIT_MATCH_BLUE_INVOICE.getCode().equals(code)
                || WAIT_SPLIT_INVOICE.getCode().equals(code)
                || WAIT_MATCH_TAX_CODE.getCode().equals(code);
    }
}
