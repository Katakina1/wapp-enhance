package com.xforceplus.wapp.client;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class NbrRsp {
    private String code;
    private String message;
    private List<JSONObject> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HyperNbr {
        private String itemNbr;
        private String mdsDescSizs;
        private String mdsDescUnit;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SamsNbr {
        private AttributesDTO attributes;

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class AttributesDTO {
            private String itemNbr;
            private String sellingUnit;
            private String specification;
        }

        public String getSellingUnit() {
            return Objects.nonNull(attributes) ? attributes.getSellingUnit() : null;
        }

        public String getSpecification() {
            return Objects.nonNull(attributes) ? attributes.getSpecification() : null;
        }

        public String getItemNbr() {
            return Objects.nonNull(attributes) ? attributes.getItemNbr() : null;
        }

    }
}
