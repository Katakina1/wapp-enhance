package com.xforceplus.wapp.modules.cost.entity;

import java.io.Serializable;

public class SelectionOptionEntity implements Serializable {
    private String optionKey;
    private String optionName;

    public String getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
