package com.xforceplus.wapp.modules.collect.pojo;

import com.xforceplus.wapp.modules.job.pojo.Authorize;
import com.xforceplus.wapp.modules.job.pojo.BasePojo;
import com.xforceplus.wapp.modules.job.pojo.GlobalInfo;

/**
 * @author Colin.hu
 * @date 4/16/2018
 */
public class BaseQueJson extends BasePojo {

    private static final long serialVersionUID = 3934885822859136457L;

    private GlobalInfo globalInfo;
    private Authorize authorize;
    private String data;

    public GlobalInfo getGlobalInfo() {
        return globalInfo;
    }

    public void setGlobalInfo(GlobalInfo globalInfo) {
        this.globalInfo = globalInfo;
    }

    public Authorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Authorize authorize) {
        this.authorize = authorize;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
