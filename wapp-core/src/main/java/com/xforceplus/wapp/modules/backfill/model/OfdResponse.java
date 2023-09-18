package com.xforceplus.wapp.modules.backfill.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description ofd response
 * @create 2021-09-15 20:24
 **/
@Setter
@Getter
public class OfdResponse {
    public static final String CODE_OF_OK = "TXWRVC0001";
    private String code;
    private String message;
    private OfdResponseResult result;


    @Setter
    @Getter
    public static class OfdResponseResult {
        private String centralTaxStatus;
        private String checkStatus;
        private String checkRedFlag;
        private String checkComOfd;
        private String checkComOfdInfo;
        private InvoiceMain invoiceMain;

        private String imageUrl;
        private List<InvoiceDetail> invoiceDetails;
    }

    public boolean isOk() {
        return Objects.equals(CODE_OF_OK, this.code);
    }
}
