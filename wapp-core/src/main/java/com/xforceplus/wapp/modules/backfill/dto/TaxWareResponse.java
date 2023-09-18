package com.xforceplus.wapp.modules.backfill.dto;

import lombok.Data;

import java.util.Objects;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class TaxWareResponse {
    public static final String OK = "TXWRVC0001";

    private String code;
    private String message;
    private AnalysisXmlResult result;

    public boolean isOK() {
        return Objects.equals(OK, this.code);
    }
}
