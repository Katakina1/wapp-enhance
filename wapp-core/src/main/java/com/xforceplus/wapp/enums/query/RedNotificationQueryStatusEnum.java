package com.xforceplus.wapp.enums.query;


import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.common.dto.param.RedNotificationQueryParam;

import java.util.Arrays;
import java.util.List;

/**
 * Describe: 红字信息表查询状态
 * 待申请   0
 * 已申请   1
 * 申请中   2
 * 申请失败  3
 * 撤销中   4
 * 撤销失败  5
 * 已撤销   6
 * 无需申请 -1
 *
 * @Author xiezhongyong
 * @Date 2022/9/7
 */
public enum RedNotificationQueryStatusEnum {


    WAIT_TO_APPLY(0, "待申请",
            RedNotificationQueryParam.builder().applyingStatus(
                    Arrays.asList(
                            RedNoApplyingStatus.WAIT_TO_APPLY.getValue())
            ).build()
    ),


    APPLYING(1, "申请中",
            RedNotificationQueryParam.builder().applyingStatus(
                    Arrays.asList(
                            RedNoApplyingStatus.APPLYING.getValue())
            ).build()
    ),

    APPLIED(2, "已申请",
            RedNotificationQueryParam.builder().applyingStatus(
                    Arrays.asList(
                            RedNoApplyingStatus.APPLYING.getValue())
            ).build()
    ),

    APPLY_FAIL(3, "申请失败",
            RedNotificationQueryParam.builder().applyingStatus(
                    Arrays.asList(
                            RedNoApplyingStatus.APPLYING.getValue())
            ).build()
    ),

    REVOKEING(4, "撤销中",
            RedNotificationQueryParam.builder().applyingStatus(
                    Arrays.asList(
                            RedNoApplyingStatus.WAIT_TO_APPLY.getValue())
            ).build()
    ),
    REVOKE_FAIL(5, "撤销失败",
            RedNotificationQueryParam.builder().applyingStatus(
                    Arrays.asList(
                            RedNoApplyingStatus.WAIT_TO_APPLY.getValue())
            ).build()
    ),

    ALREADY_REVOKE(6, "已撤销",
            RedNotificationQueryParam.builder().applyingStatus(
                    Arrays.asList(
                            RedNoApplyingStatus.APPLIED.getValue())
            ).approveStatus(
                    Arrays.asList(ApproveStatus.ALREADY_ROLL_BACK.getValue())
            ).build()
    ),

    /**
     * 无需申请
     */
    NON_APPLY(-1, "无需申请",
            RedNotificationQueryParam.builder().build()
    ),


    ;

    RedNotificationQueryStatusEnum(Integer code, String message, RedNotificationQueryParam... params) {
        this.code = code;
        this.message = message;
        this.queryParams = null == params ? null : Arrays.asList(params);
    }

    private Integer code;
    private String message;
    private List<RedNotificationQueryParam> queryParams;

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }

    public List<RedNotificationQueryParam> queryParams() {
        return queryParams;
    }

    public static RedNotificationQueryStatusEnum fromCode(Integer code) {
        return Arrays.stream(values()).filter(s -> s.code().equals(code))
                .findAny().orElse(null);
    }


}
