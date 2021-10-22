package com.xforceplus.wapp.modules.settlement.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-22 14:27
 **/
@Setter
@Getter
public class InvoiceMatchedRequest {
    private List<Invoice> invoices;


    @Setter
    @Getter
    public static class Invoice{
        String invoiceNo;
        String invoiceCode;
    }
}
