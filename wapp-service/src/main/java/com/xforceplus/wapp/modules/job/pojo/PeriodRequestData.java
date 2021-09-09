package com.xforceplus.wapp.modules.job.pojo;

import java.util.Date;

public class PeriodRequestData {

    private Authorize authorize;
    private GlobalInfo globalInfo;
    private String data;

    public Authorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Authorize authorize) {
        this.authorize = authorize;
    }

    public GlobalInfo getGlobalInfo() {
        return globalInfo;
    }

    public void setGlobalInfo(GlobalInfo globalInfo) {
        this.globalInfo = globalInfo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}