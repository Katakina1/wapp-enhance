package com.xforceplus.wapp.modules.job.pojo;

/**
 * ORC识别查询服务授权信息
 *
 * Created by Daily.zhang on 2018/04/20.
 */
public class OcrDiscAuthorize extends BasePojo {
    private static final long serialVersionUID = 2570402555802619093L;

    /**
     * 授权key    企业获得接入资格时获得
     */
    private String appKey;

    /**
     * 授权码  企业获得接入资格时获得
     */
    private String appSec;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSec() {
        return appSec;
    }

    public void setAppSec(String appSec) {
        this.appSec = appSec;
    }
}
