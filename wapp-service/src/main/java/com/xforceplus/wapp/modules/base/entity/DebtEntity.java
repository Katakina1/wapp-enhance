package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 债务公告函实体类
 */
public class DebtEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = 9080184315562904000L;

    //债务类别
    private String debtType;

    //是否已读（0-未读 1-已读）
    private String haveRead;

    //是否已发布(0-未发布 1-已发布)
    private String isRelease;

    //供应商号码
    private String venderId;

    //部门号
    private String deptNo;

    //订单号
    private String orderNo;

    //店号
    private String store;

    //收货日期
    private Date receiveDate;

    //商品价格下调日期
    private Date goodsReduceDate;

    //商品号
    private String goodsNo;

    //商品名称
    private String goodsName;

    //收货数量
    private Integer receiveNum;

    //包装数量
    private Integer packageNum;

    //商品实际结算价格
    private BigDecimal goodsActualPrice;

    //商品下调后的价格
    private BigDecimal priceReduceAfter;

    //订单折扣
    private BigDecimal orderDiscount;

    //税率
    private BigDecimal taxRate;

    //价格调整商品应付差额
    private BigDecimal priceDifference;

    //商品下调前的价格
    private BigDecimal priceReduceBefore;

    //商品价格下调日库存数量
    private Integer reduceStockNum;

    //协议号
    private String protocolNo;

    //协议号金额
    private BigDecimal protocolAmount;

    //库存商品补偿金
    private BigDecimal compensationAmount;

    //导入人
    private String createBy;

    //上传时间
    private Date uploadDate;

    //导入失败原因
    private String failureReason;

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public BigDecimal getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(BigDecimal priceDifference) {
        this.priceDifference = priceDifference;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDebtType() {
        return debtType;
    }

    public void setDebtType(String debtType) {
        this.debtType = debtType;
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

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getGoodsReduceDate() {
        return goodsReduceDate;
    }

    public void setGoodsReduceDate(Date goodsReduceDate) {
        this.goodsReduceDate = goodsReduceDate;
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

    public Integer getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(Integer receiveNum) {
        this.receiveNum = receiveNum;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public BigDecimal getGoodsActualPrice() {
        return goodsActualPrice;
    }

    public void setGoodsActualPrice(BigDecimal goodsActualPrice) {
        this.goodsActualPrice = goodsActualPrice;
    }

    public BigDecimal getPriceReduceAfter() {
        return priceReduceAfter;
    }

    public void setPriceReduceAfter(BigDecimal priceReduceAfter) {
        this.priceReduceAfter = priceReduceAfter;
    }

    public BigDecimal getOrderDiscount() {
        return orderDiscount;
    }

    public void setOrderDiscount(BigDecimal orderDiscount) {
        this.orderDiscount = orderDiscount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getPriceReduceBefore() {
        return priceReduceBefore;
    }

    public void setPriceReduceBefore(BigDecimal priceReduceBefore) {
        this.priceReduceBefore = priceReduceBefore;
    }

    public Integer getReduceStockNum() {
        return reduceStockNum;
    }

    public void setReduceStockNum(Integer reduceStockNum) {
        this.reduceStockNum = reduceStockNum;
    }

    public String getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
    }

    public BigDecimal getProtocolAmount() {
        return protocolAmount;
    }

    public void setProtocolAmount(BigDecimal protocolAmount) {
        this.protocolAmount = protocolAmount;
    }

    public BigDecimal getCompensationAmount() {
        return compensationAmount;
    }

    public void setCompensationAmount(BigDecimal compensationAmount) {
        this.compensationAmount = compensationAmount;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
