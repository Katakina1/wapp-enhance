package com.xforceplus.wapp.enums.settlement;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 结算单审核类型
 * @date : 2022/09/27 15:21
 **/
@AllArgsConstructor
@Getter
public enum SettlementApproveTypeEnum {

    /**
     * 结算单进入审核状态原因 1,2-待开票  3-部分开票/已开票
     */
    DEFAULT(0, "默认"),
    INVOICE_LIMIT_AMOUNT_UPDATE(1, "开票限额修改"),
    AMOUNT_WRONG(2, "金额有误"),
    BLUE_FLUSH(3, "蓝冲");

    private Integer code;
    private String desc;

    public static SettlementApproveTypeEnum fromCode(Integer code) {
        return Arrays.stream(SettlementApproveTypeEnum.values()).filter(typeEnum -> typeEnum.getCode().equals(code)).findFirst().orElse(null);
    }
}
