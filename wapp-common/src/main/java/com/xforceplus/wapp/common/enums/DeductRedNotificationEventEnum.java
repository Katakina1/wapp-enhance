package com.xforceplus.wapp.common.enums;

import java.util.Arrays;
import java.util.List;

public enum DeductRedNotificationEventEnum {
    /**
     * 预制发票创建
     */
    PRE_INVOICE_CREATED{
        @Override
        public AgreementRedNotificationStatus toRedNotificationApplyStatus() {
            return AgreementRedNotificationStatus.APPLY_PENDING;
        }
    },
    /**
     * 发起红字信息表申请
     */
    APPLY_NOTIFICATION {
        @Override
        public AgreementRedNotificationStatus toRedNotificationApplyStatus() {
            return AgreementRedNotificationStatus.APPLYING;
        }

        @Override
        public List<AgreementRedNotificationStatus> applyStatusParams() {
            // 只能由  待申请->申请中（防止消息延时导致申请中把已申请给覆盖了）
            return Arrays.asList(AgreementRedNotificationStatus.APPLY_PENDING);
        }
    },
    /**
     * 申请成功
     */
    APPLY_SUCCEED {
        @Override
        public AgreementRedNotificationStatus toRedNotificationApplyStatus() {
            return AgreementRedNotificationStatus.APPLIED;
        }
    },
    /**
     * 申请失败
     */
    APPLY_FAILED {
        @Override
        public AgreementRedNotificationStatus toRedNotificationApplyStatus() {
            return AgreementRedNotificationStatus.APPLY_FAILED;
        }
    },
    /**
     * 发起撤销
     */
    REVOCATION_APPLY {
        @Override
        public AgreementRedNotificationStatus toRedNotificationApplyStatus() {
            return AgreementRedNotificationStatus.IN_REVOCATION;
        }
    },
    /**
     * 撤销成功
     */
    REVOCATION_SUCCEED {
        @Override
        public AgreementRedNotificationStatus toRedNotificationApplyStatus() {
            return AgreementRedNotificationStatus.REVOKED;
        }
    },
    /**
     * 撤销失败（审核被拒绝）
     */
    REVOCATION_REJECTED {
        @Override
        public AgreementRedNotificationStatus toRedNotificationApplyStatus() {
            return AgreementRedNotificationStatus.REVOCATION_FAILED;
        }
    },
    /**
     * 撤销失败（税件返回失败）
     */
    REVOCATION_FAILED {
        @Override
        public AgreementRedNotificationStatus toRedNotificationApplyStatus() {
            return AgreementRedNotificationStatus.REVOCATION_FAILED;
        }
    },
    /**
     * 预制发票作废
     */
    PRE_INVOICE_DISCARD,
    /**
     * 预制发票删除
     */
    PRE_INVOICE_DELETE,

    /**
     * 上传红票
     */
    UPLOAD_RED_INVOICE,

    /**
     * 删除红票
     */
    DELETE_RED_INVOICE;

    @Override
    public String toString() {
        return this.name();
    }

    public AgreementRedNotificationStatus toRedNotificationApplyStatus(){
        return null;
    }
    public AgreementRedNotificationStatus toRedNotificationApplyStatus(boolean isRequiredApply){
        return isRequiredApply?toRedNotificationApplyStatus():AgreementRedNotificationStatus.NON_REQUIRE;
    }

    public List<AgreementRedNotificationStatus> applyStatusParams(){
        return null;
    }
}
