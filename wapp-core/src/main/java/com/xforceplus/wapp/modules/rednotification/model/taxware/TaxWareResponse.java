package com.xforceplus.wapp.modules.rednotification.model.taxware;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TaxWareResponse {

    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("traceId")
    private Object traceId;
    @JsonProperty("result")
    private ResultDTO result;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("serialNo")
        private String serialNo;

        @JsonProperty("pdfUrl")
        private String pdfUrl;

    }

}
