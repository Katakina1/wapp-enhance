package com.xforceplus.wapp.modules.backFill.model;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-16 11:36
 **/
@Data
public class VerificationBack {
    public static final String OK = "TXWRVC0001";

    private String code;
    private String message;
    private VerificationResult result;
    private String taskId;

    public boolean isOK() {
        return Objects.equals(OK, this.code);
    }

    @Data
    public static class VerificationResult{
        private InvoiceMain invoiceMain;
        private List<InvoiceDetail> invoiceDetails;
        private String invoiceType;
    }
}
