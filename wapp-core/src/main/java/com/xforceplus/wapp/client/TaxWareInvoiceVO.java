package com.xforceplus.wapp.client;

import lombok.Data;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class TaxWareInvoiceVO {
    private String invoiceCode;
    private String invoiceNo;
    private String invoiceType;
    private String paperDrewDate;
    private String status;
    private String changeType;
    private String checkKey;
}
