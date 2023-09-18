package com.xforceplus.wapp.modules.exchangeTicket.dto;

import lombok.Data;

/**
 * @author aiwentao@xforceplus.com
 */
@Data
public class ExchangeTicketQueryDto {

    private String jvCode;
    private String venderId;
    private String voucherNo;
    private String authStatus;
    private String invoiceNo;
    private String invoiceCode;
    private String flowType;
    private String exchangeStatus;

    private String exchangeRemark;
    private String exchangeSoource;
    private String exchangeInvoiceNo;
    private String exchangeType;

    private String taxRate;
}
