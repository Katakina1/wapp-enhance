package com.xforceplus.wapp.enums;

import lombok.Getter;

/**
 * 结算单状态
 */
public enum TXfSettlementStatusEnum {

    WAIT_CONFIRM(1,"待确认"),
    NO_UPLOAD_RED_INVOICE(2,"待开票"),
    UPLOAD_HALF_RED_INVOICE(3,"已开部分票"),
    UPLOAD_RED_INVOICE(4,"已开票"),
    FINISH(5,"已完成"),
    WAIT_CHECK(6,"待审核"),
    CANCEL(7,"已撤销"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;

    TXfSettlementStatusEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
