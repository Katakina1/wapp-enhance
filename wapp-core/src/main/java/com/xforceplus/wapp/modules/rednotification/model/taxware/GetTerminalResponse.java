package com.xforceplus.wapp.modules.rednotification.model.taxware;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class GetTerminalResponse {

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
        @JsonProperty("terminalList")
        private List<TerminalListDTO> terminalList;

        @NoArgsConstructor
        @Data
        public static class TerminalListDTO {
            @JsonProperty("terminalUn")
            private String terminalUn;
            @JsonProperty("terminalType")
            private Integer terminalType;
            @JsonProperty("directOnlineFlag")
            private Boolean directOnlineFlag;
            @JsonProperty("terminalName")
            private String terminalName;
            @JsonProperty("invoiceTypeList")
            private List<String> invoiceTypeList;
            @JsonProperty("status")
            private Integer status;
            @JsonProperty("onlineDeviceList")
            private List<DeviceDTO> onlineDeviceList;
        }

        @NoArgsConstructor
        @Data
        public static class DeviceDTO {
            String deviceUn;
            List<String> invoiceTypeList;
        }
    }
}
