package com.xforceplus.wapp.client;

import lombok.Data;

import java.util.List;

/**
 * @author masp mashaopeng@xforceplus.com
 */
@Data
public class TaxWareInvoiceRsp {
    private String code;
    private String message;
    private Result result;

    @Data
    public static class Result {
        private TaxWareInvoice invoiceMain;
        private List<TaxWareInvoiceDetail> invoiceDetails;
    }
}