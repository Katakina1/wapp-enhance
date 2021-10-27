package com.xforceplus.wapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by SunShiyong on 2021/10/25.
 * 日志操作枚举
 */
@AllArgsConstructor
@Getter
public enum OperateLogEnum {

    CREATE_SETTLEMENT("01", 0,"创建结算单"),
    APPLY_VERDICT("02",0,"申请不定案"),
    PASS_VERDICT("03",0,"不定案通过"),
    REJECT_VERDICT("04",0,"不定案驳回"),
    CONFIRM_SETTLEMENT("05",0,"确认结算单"),
    CANCEL_RED_NOTIFICATION_APPLY("06",0,"撤销红字信息表申请"),
    APPLY_RED_NOTIFICATION("07",0,"申请红字信息表（即重新拆票）"),
    UPLOAD_INVOICE("08",0,"上传红票"),
    RECEIVED_INVOICE("09",0,"红票已签收"),

    CREATE_DEDUCT("10",1,"索赔单创建"),
    CREATE_AGREEMENT("11",1,"协议单创建"),
    LOCK_AGREEMENT("12",1,"协议单锁定"),
    UNLOCK_AGREEMENT("13",1,"协议单解锁"),
    CANCEL_AGREEMENT("14",1,"协议单撤销"),
    CREATE_EPD("15",1,"EPD单创建"),
    LOCK_EPD("16",1,"EPD单锁定"),
    UNLOCK_EPD("17",1,"EPD单解锁"),
    CANCEL_EPD("18",1,"EPD单撤销");
    private String operateCode;
    private Integer operateType;
    private String operateDesc;
}
