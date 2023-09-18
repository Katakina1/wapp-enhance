package com.xforceplus.wapp.modules.backfill.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-15 17:25
 **/
@Setter
@Getter
public class VerificationRequest {
    /**
     * 发票代码
     */
    private String invoiceCode;
    /**
     * 发票号码
     */
    private String invoiceNo;
    /**
     * 校验码，必填票种：增值税普通发票、增值税电子普通发票、增值税普通发票（卷式）、增值税电子普通发票（通行费）、增值税电子普通发票、深圳及北京区块链电子普通发票
     */
    private String checkCode;
    /**
     * 开票日期
     */
    private String paperDrewDate;
    /**
     * 不含税金额，必填票种：增值税专用发票、机动车销售统一发票、二手车销售统一发票、增值税电子专用发票、深圳区块链电子普通发票
     */
    private String amount;

    private String amountWithTax;
    /**
     * 验真级别，非必填 0-有缓存(默认)，1-不缓存，每次请求同步国税最新数据
     */
    private String yzLevel;
    /**
     * 租户或分组代码或系统标识
     */
    private String tenantCode;
    /**
     * 非必填，回调可用、批量操作回传
     */
    private String customerNo;
}
