package com.xforceplus.wapp.enums.settlement;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 结算单审核状态
 * @date : 2022/09/27 15:52
 **/
@AllArgsConstructor
@Getter
public enum SettlementApproveStatusEnum {

    /**
     * 结算单审核状态
     */
    DEFAULT(0, "默认"),
    APPROVING(1, "待审核"),
    APPROVE_SUCCESS(2, "审核通过"),
    APPROVE_REJECTED(3, "审核不通过");

    private Integer code;
    private String desc;

    /**
     * 非审核流程状态
     */
    public static boolean isApprove(Integer code) {
        return APPROVING.getCode().equals(code) || APPROVE_REJECTED.getCode().equals(code) || APPROVE_SUCCESS.getCode().equals(code);
    }
}
