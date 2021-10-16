package com.xforceplus.wapp.modules.backFill.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-15 19:04
 **/
@Setter
@Getter
public class OfdParseRequest {
    private String tenantCode;
    private String ofdEncode;
    private String type;
}
