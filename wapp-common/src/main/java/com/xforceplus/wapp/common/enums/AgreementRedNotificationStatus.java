package com.xforceplus.wapp.common.enums;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * 协议单据在红字信息表周期流转的状态
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-09-14 14:16
 **/
@Getter
@ToString
public enum AgreementRedNotificationStatus {
    NON_REQUIRE(0,"无需申请"),
    APPLY_PENDING(1,"待申请"),
    APPLIED(2,"已申请"),
    APPLYING(3,"申请中"),
    APPLY_FAILED(4,"申请失败"),
    IN_REVOCATION(5,"撤销中"),
    REVOCATION_FAILED(6,"撤销失败"),
    REVOKED(7,"已撤销");

    private final int value;
    private final String desc;
    AgreementRedNotificationStatus(int value,String desc){
        this.value=value;
        this.desc=desc;
    }

    public static AgreementRedNotificationStatus fromValue(int value) {
        return Arrays.stream(values()).filter(s -> s.getValue() == value)
                .findAny().orElse(null);
    }

}
