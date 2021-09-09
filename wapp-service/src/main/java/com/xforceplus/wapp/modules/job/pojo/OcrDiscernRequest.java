package com.xforceplus.wapp.modules.job.pojo;

/**
 *
 * OCR识别服务查询请求
 *
 * Created by Daily.zhang on 2018/04/20.
 */
public class OcrDiscernRequest extends BasePojo {
    private static final long serialVersionUID = -1449588311327751387L;

    /**
     * 授权信息
     */
    private OcrDiscAuthorize authorize;

    /**
     * 全局信息
     */
    private OcrDiscGlobalInfo globalInfo;

    /**
     * 图片信息
     */
    private String picture;

    /**
     * 要识别的发票类型
     */
//    private String invoiceType;

    public OcrDiscAuthorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(OcrDiscAuthorize authorize) {
        this.authorize = authorize;
    }

    public OcrDiscGlobalInfo getGlobalInfo() {
        return globalInfo;
    }

    public void setGlobalInfo(OcrDiscGlobalInfo globalInfo) {
        this.globalInfo = globalInfo;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
