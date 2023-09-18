package com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.send;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 发票原件签收状态反馈
 * @Author: ChenHang
 * @Date: 2023/6/27 15:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendInvoiceReceipt implements Serializable {

    /**
     * 发票号码
     */
    private String invoiceCode;
    /**
     * 发票代码
     */
    private String invoiceNo;
    /**
     * 发票类型
     */
    private String invoiceType;
    /**
     * 签收时间
     */
    private String qsDate;
    /**
     * 签收状态
     * Y-是,签收成功
     * N-否,签收失败
     */
    private String qsStatus;
    /**
     * 签收备注
     */
    private String remark;
}
