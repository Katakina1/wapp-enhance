package com.xforceplus.wapp.modules.job.pojo;

public class ResponesJson extends BasePojo{

    private GlobalInfo globalInfo;
    private ReturnStateInfo returnStateInfo;
    private String data;

    public GlobalInfo getGlobalInfo() {
        return globalInfo;
    }

    public void setGlobalInfo(GlobalInfo globalInfo) {
        this.globalInfo = globalInfo;
    }

    public ReturnStateInfo getReturnStateInfo() {
        return returnStateInfo;
    }

    public void setReturnStateInfo(ReturnStateInfo returnStateInfo) {
        this.returnStateInfo = returnStateInfo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
