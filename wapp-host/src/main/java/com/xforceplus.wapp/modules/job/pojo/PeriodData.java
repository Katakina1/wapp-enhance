package com.xforceplus.wapp.modules.job.pojo;

import java.util.Date;

public class PeriodData {
    private String taxno;

    public String getTaxno() {
        return taxno;
    }

    public void setTaxno(String taxno) {
        this.taxno = taxno;
    }

    @Override
    public String toString() {
        return "PeriodData{" +
                "taxno='" + taxno + '\'' +
                '}';
    }
}