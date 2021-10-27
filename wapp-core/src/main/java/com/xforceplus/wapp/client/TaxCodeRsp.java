package com.xforceplus.wapp.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class TaxCodeRsp {
    private String code;
    private String message;
    private List<ResultBean> result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultBean {
        private String itemName;
        private String itemShortName;
        private String goodsTaxNo;
        private String remark;
        private String taxCode;
        private String taxName;
        private String taxShortName;
        private String taxPolicy;
        private String taxCodeVersion;
        private List<String> taxRateList;
        private String specialManagement;
        private String splitCode;
    }
}
