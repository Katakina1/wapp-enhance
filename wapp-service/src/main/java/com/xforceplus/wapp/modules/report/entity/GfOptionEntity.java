package com.xforceplus.wapp.modules.report.entity;

import java.io.Serializable;
/**
 * 购方下拉选项实体
 */
public class GfOptionEntity implements Serializable {
    private String value;
    private String label;
    private String orgCode;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
}
