package com.xforceplus.wapp.modules.base.entity;

import java.math.BigDecimal;
import java.util.Date;

public class CustomPFREntity extends BaseEntity{
    //id
    private Long id ;
    //是否已发布(0-未发布 1-已发布)
    private String is_release;
    //是否已读（0-未读 1-已读）
    private String haveRead;
    //创建人
    private String createBy;
    //发布时间
    private Date releasetime;
    //上传时间
    private Date uploadDate;
    //部门号
    private String deptNo;
    //供应商号码
    private String venderId;
    //供应商名称
    private String venderName;
    //订单号
    private String orderNo;
    //商品号
    private String goodsNo;
    //商品描述
    private String goodsName;
    //订单取消日期
    private Date orderCancelDate;
    //未送齐货金额（含税）
    private String notFullGoodsAmount;
    //合同违约金比率
    private String contractBreakRate;
    //合同生效时间
    private Date contarctEffectDate;
    //订单折扣
    private BigDecimal orderDiscount;
    //应收违约金（含税）
    private BigDecimal breakAmount;
    //失败原因
    private String failureReason;

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIs_release() {
        return is_release;
    }

    public void setIs_release(String is_release) {
        this.is_release = is_release;
    }

    public String getHaveRead() {
        return haveRead;
    }

    public void setHaveRead(String haveRead) {
        this.haveRead = haveRead;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getReleasetime() {
        return releasetime;
    }

    public void setReleasetime(Date releasetime) {
        this.releasetime = releasetime;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Date getOrderCancelDate() {
        return orderCancelDate;
    }

    public void setOrderCancelDate(Date orderCancelDate) {
        this.orderCancelDate = orderCancelDate;
    }

    public String getNotFullGoodsAmount() {
        return notFullGoodsAmount;
    }

    public void setNotFullGoodsAmount(String notFullGoodsAmount) {
        this.notFullGoodsAmount = notFullGoodsAmount;
    }

    public String getContractBreakRate() {
        return contractBreakRate;
    }

    public void setContractBreakRate(String contractBreakRate) {
        this.contractBreakRate = contractBreakRate;
    }

    public Date getContarctEffectDate() {
        return contarctEffectDate;
    }

    public void setContarctEffectDate(Date contarctEffectDate) {
        this.contarctEffectDate = contarctEffectDate;
    }

    public BigDecimal getOrderDiscount() {
        return orderDiscount;
    }

    public void setOrderDiscount(BigDecimal orderDiscount) {
        this.orderDiscount = orderDiscount;
    }

    public BigDecimal getBreakAmount() {
        return breakAmount;
    }

    public void setBreakAmount(BigDecimal breakAmount) {
        this.breakAmount = breakAmount;
    }
}
