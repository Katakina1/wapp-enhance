package com.xforceplus.wapp.modules.job.enumflord;

import lombok.Getter;

/**
 * CreateBy leal.liang on 2018/4/23.
 **/
@Getter
public enum SignInEnum {

    NUMBER_ZERO("0"),

    NUMBER_ONE("1"),

    NUMBER_TWO("2"),

    NUMBER_THREE("3"),

    NUMBER_FOUR("4"),

    NUMBER_FIVE("5"),

    NUMBER_SIX("6"),

    //发票状态--正常
    INVOICE_STATUS_YES("N"),

    //发票状态--作废
    INVOICE_STATUS_NO("Y"),

    /**
     * 签收状态-成功
     */
    QS_SUCCESS("1"),

    /**
     * 签收状态-失败
     */
    QS_FAIL("0"),

    /**
     * 签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收)
     */
    //手工签收
    QS_HANDWORK("4"),

    //扫码签收
    QS_SWEEP_CODE("0"),

    //app签收
    QS_PHONE_APP("2"),

    //导入签收
    QS_LEADING_IN("3"),

    //扫描仪签收
    QS_SCANNER("1");

    private String value;

    SignInEnum(String value) {
        this.value = value;
    }
}
