package com.xforceplus.wapp.modules.backFill.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-15 17:28
 **/
@Setter
@Getter
public class VerificationResponse {

    public static final String OK = "TXWRVC0001";

    private String code;
    private String message;
    private String result;

    public boolean isOK() {
        return Objects.equals(OK, this.code);
    }

}
