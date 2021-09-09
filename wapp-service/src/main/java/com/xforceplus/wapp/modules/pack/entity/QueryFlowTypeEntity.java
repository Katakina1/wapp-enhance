package com.xforceplus.wapp.modules.pack.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * 抵账表实体(发票签收)
 */
public class QueryFlowTypeEntity implements Serializable {
    private String value;
    private String label;
    //业务类型名称
    private  String billTypeName;

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

    public String getBillTypeName() {
        return billTypeName;
    }

    public void setBillTypeName(String billTypeName) {
        this.billTypeName = billTypeName;
    }
}
