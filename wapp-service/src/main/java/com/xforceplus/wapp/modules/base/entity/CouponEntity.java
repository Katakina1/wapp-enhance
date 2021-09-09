package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Coupon公告函实体类
 */
public class CouponEntity extends AbstractBaseDomain {

    //6D
    private String sixD;

    //8D
    private String eightD;

    //供应商名称
    private String venderName;

    //现金房定案日期
    private Date caseDate;

    //店号
    private String store;

    //优惠券商品号
    private String couponNo;

    //9D
    private String nineD;

    //小票说明
    private String ticketDesc;

    //开始日期
    private Date startDate;

    //结束日期
    private Date endDate;

    //优惠券使用次数
    private Integer couponCount;

    //现金房定案金额
    private BigDecimal caseAmount;

    //承担比例
    private String assumeScale;

    //应收金额
    private BigDecimal receivableAmount;

    //优惠券说明
    private String couponDesc;

    //是否已读（0-未读 1-已读）
    private String haveRead;

    //是否已发布(0-未发布 1-已发布)
    private String isRelease;

    //导入人
    private String createBy;

    //上传时间
    private Date uploadDate;

    //导入失败原因
    private String failureReason;

    public String getSixD() {
        return sixD;
    }

    public void setSixD(String sixD) {
        this.sixD = sixD;
    }

    public String getEightD() {
        return eightD;
    }

    public void setEightD(String eightD) {
        this.eightD = eightD;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public Date getCaseDate() {
        return caseDate;
    }

    public void setCaseDate(Date caseDate) {
        this.caseDate = caseDate;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public String getNineD() {
        return nineD;
    }

    public void setNineD(String nineD) {
        this.nineD = nineD;
    }

    public String getTicketDesc() {
        return ticketDesc;
    }

    public void setTicketDesc(String ticketDesc) {
        this.ticketDesc = ticketDesc;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(Integer couponCount) {
        this.couponCount = couponCount;
    }

    public BigDecimal getCaseAmount() {
        return caseAmount;
    }

    public void setCaseAmount(BigDecimal caseAmount) {
        this.caseAmount = caseAmount;
    }

    public String getAssumeScale() {
        return assumeScale;
    }

    public void setAssumeScale(String assumeScale) {
        this.assumeScale = assumeScale;
    }

    public BigDecimal getReceivableAmount() {
        return receivableAmount;
    }

    public void setReceivableAmount(BigDecimal receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    public String getCouponDesc() {
        return couponDesc;
    }

    public void setCouponDesc(String couponDesc) {
        this.couponDesc = couponDesc;
    }

    public String getHaveRead() {
        return haveRead;
    }

    public void setHaveRead(String haveRead) {
        this.haveRead = haveRead;
    }

    public String getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(String isRelease) {
        this.isRelease = isRelease;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
