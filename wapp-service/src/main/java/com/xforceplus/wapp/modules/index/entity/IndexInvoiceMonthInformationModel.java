package com.xforceplus.wapp.modules.index.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Bobby
 * @date 2018/4/17
 * 首页-本月发票信息
 */
@Getter
@Setter
@ToString
public final class IndexInvoiceMonthInformationModel {

    /**
     * 本月新增发票统计
     */
    private Integer xzInvoice;

    /**
     * 本月已认证发票统计
     */
    private Integer yrzInvoice;

    /**
     * 本月已认证发票金额（未税）
     */
    private String taxAmountCount;

    /**
     * 本月已认证发票税额
     */
    private String invoiceAmountCount;
}
