package com.xforceplus.wapp.client;

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
    public static class ResultBean {
        private String errorCode;
        private String msg;
        private TaxCodeBean data;
    }
}
