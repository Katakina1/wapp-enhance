package com.xforceplus.wapp.modules.job.pojo;

/**
 * OCR识别查询服务全局信息
 *
 * Created by Daily.zhang on 2018/04/20.
 */
public class OcrDiscGlobalInfo extends BasePojo {
    private static final long serialVersionUID = 7332845895433517765L;

    /**
     * 应用识别码    固定为：FPBX    必填
     */
    private String appId;

    /**
     * 接口版本 当前 v1.0 必填
     */
    private String version;

    /**
     * 企业代码     企业获得接入资格时获得：JXFP
     */
    private String enterpriseCode;

    /**
     * 用户Id     建议手机号，用于区分用户
     */
    private String userId;

    /**
     * 图片标识     可为随机字符串
     */
    private String uuid;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
