package com.xforceplus.wapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * invoiceOrigin
 * 红字信息审批状态  操作类型  1 申请 2 同步 3 撤销 4 删除
 * 5.撤销待审批  单独页面审批
 * approve_status
 */
@Getter
@AllArgsConstructor
public enum ApplyType implements ValueEnum<Integer>{
    APPLY(1,"申请"),
    SYNC(2,"同步"),
    ROLL_BACK(3,"撤销");

    private final Integer value;
    private final String desc;

}
