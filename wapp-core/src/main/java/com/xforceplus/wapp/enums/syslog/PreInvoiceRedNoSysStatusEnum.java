package com.xforceplus.wapp.enums.syslog;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 红字状态事件状态
 * @date : 2022/09/30 10:52
 **/
@Getter
@AllArgsConstructor
public enum PreInvoiceRedNoSysStatusEnum {

    /**
     * 0-发送 1-接收
     */
    SEND("0", "发送"),
    RECEIVE("1", "接收");

    private String code;

    private String desc;
}
