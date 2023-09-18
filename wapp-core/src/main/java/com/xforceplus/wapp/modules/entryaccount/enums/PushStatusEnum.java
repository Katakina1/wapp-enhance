package com.xforceplus.wapp.modules.entryaccount.enums;

import lombok.Getter;

/**
 * 推送bms状态
 * @Author: ChenHang
 * @Date: 2023/7/3 16:04
 */
@Getter
public enum PushStatusEnum {

    SUCCESS("1", "推送成功"),

    UNHANDLED("0", "未推送"),

    fail("-1", "推送失败");

    /**
     * 推送BMS海关票匹配状态
     */
    private String matchState;

    private String resultTip;

    PushStatusEnum(String matchState, String resultTip) {
        this.matchState = matchState;
        this.resultTip = resultTip;
    }

}
