package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;
import java.util.List;

/**
 * 红冲明细表
 */
public class RedTicketMatchDetail extends AbstractBaseDomain {

    private static final long serialVersionUID = 7710439962740602519L;
    private String goodsName;// 货物或应税劳务名称
    private String goodsModel;//规格型号
    private String goodsUnit;//单位
    private Integer goodsNumber;//数量
    private BigDecimal goodsPrice;//单价
    private BigDecimal goodsAmount;//金额
    private String taxRate;//税率
    private BigDecimal taxAmount;//税额
    private Integer redRushNumber;//红冲数量
    private BigDecimal redRushAmount;//红冲金额
    private BigDecimal redRushPrice;//红冲单价
    private String whetherRedRush;//是否红冲
    private String redTicketDataSerialNumber;//序列号
    private List<RedTicketMatchDetail> redRushDetails;

    private List<RedTicketMatchDetail> detailsnow;//当前红冲明细数据
    private List<RedTicketMatchDetail> detailbefore;//之前红冲明细数据
    private BigDecimal redRushTaxAmount;//红冲税额
    private String businessType;

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public BigDecimal getRedRushTaxAmount() {
        return redRushTaxAmount;
    }

    public void setRedRushTaxAmount(BigDecimal redRushTaxAmount) {
        this.redRushTaxAmount = redRushTaxAmount;
    }
    public BigDecimal getRedRushPrice() {
        return redRushPrice;
    }

    public void setRedRushPrice(BigDecimal redRushPrice) {
        this.redRushPrice = redRushPrice;
    }

    public String getWhetherRedRush() {
        return whetherRedRush;
    }

    public void setWhetherRedRush(String whetherRedRush) {
        this.whetherRedRush = whetherRedRush;
    }

    public String getRedTicketDataSerialNumber() {
        return redTicketDataSerialNumber;
    }

    public void setRedTicketDataSerialNumber(String redTicketDataSerialNumber) {
        this.redTicketDataSerialNumber = redTicketDataSerialNumber;
    }

    public RedTicketMatchDetail() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsModel() {
        return goodsModel;
    }

    public void setGoodsModel(String goodsModel) {
        this.goodsModel = goodsModel;
    }

    public String getGoodsUnit() {
        return goodsUnit;
    }

    public void setGoodsUnit(String goodsUnit) {
        this.goodsUnit = goodsUnit;
    }

    public Integer getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(Integer goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public BigDecimal getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Integer getRedRushNumber() {
        return redRushNumber;
    }

    public void setRedRushNumber(Integer redRushNumber) {
        this.redRushNumber = redRushNumber;
    }

    public BigDecimal getRedRushAmount() {
        return redRushAmount;
    }

    public void setRedRushAmount(BigDecimal redRushAmount) {
        this.redRushAmount = redRushAmount;
    }

    public List<RedTicketMatchDetail> getDetailsnow() { return detailsnow; }

    public List<RedTicketMatchDetail> getDetailbefore() { return detailbefore; }

    public void setDetailsnow(List<RedTicketMatchDetail> detailsnow) { this.detailsnow = detailsnow; }

    public void setDetailbefore(List<RedTicketMatchDetail> detailbefore) { this.detailbefore = detailbefore; }

    public List<RedTicketMatchDetail> getRedRushDetails() {
        return redRushDetails;
    }

    public void setRedRushDetails(List<RedTicketMatchDetail> redRushDetails) {
        this.redRushDetails = redRushDetails;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
