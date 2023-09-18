package com.xforceplus.wapp.modules.audit.enums;

import com.xforceplus.wapp.common.enums.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mashaopeng@xforceplus.com
 */
@Getter
@AllArgsConstructor
public enum AuditStatusEnum implements ValueEnum<String> {
    /**
     *  审核状态 0未审核 1审核通过 2审核不通过
     */
     NOT_AUDIT("0", "未审核"),
     AUDIT_PASS("1", "审核通过"),
     AUDIT_FAIL("2", "审核不通过"),
    ;

    private final String value;
    private final String message;
}
