package com.xforceplus.wapp.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class UpcRsp {
    private String code;
    private String message;
    private UpcVO result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpcVO {
        @JsonProperty(value = "itemNbr")
        private String itemNo;
    }
}
