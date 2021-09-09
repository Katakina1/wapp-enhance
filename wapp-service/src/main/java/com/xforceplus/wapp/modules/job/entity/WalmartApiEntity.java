package com.xforceplus.wapp.modules.job.entity;

public class WalmartApiEntity {
    private String jv;

    private String gfName;


    public String getJv() {
        return jv;
    }

    public void setJv(String jv) {
        this.jv = jv;
    }

    public String getGfName() {
        return jv+"_"+gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }
}