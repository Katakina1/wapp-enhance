package com.xforceplus.wapp.modules.base.entity;

import java.io.Serializable;

public class TaxCodeEntity extends BaseEntity implements Serializable {
    private Long id;
    private String taxSortcode ;
    private String taxName ;
    private String note ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaxSortcode() {
        return taxSortcode;
    }

    public void setTaxSortcode(String taxSortcode) {
        this.taxSortcode = taxSortcode;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
