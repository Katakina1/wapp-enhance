package com.xforceplus.wapp.modules.posuopei.entity;

import java.io.Serializable;

/**
 * @author raymond.yan
 */
public class ErrorDataEntity  implements Serializable {
    private static final long serialVersionUID = 2086842574501632534L;
    private String code;
    private String message;
    private Integer column;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public ErrorDataEntity(String code, String message, Integer column) {
        this.code = code;
        this.message = message;
        this.column = column;
    }
}
