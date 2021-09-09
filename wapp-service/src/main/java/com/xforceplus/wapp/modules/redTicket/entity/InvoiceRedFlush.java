package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by 1 on 2018/10/17 22:29
 */
public class InvoiceRedFlush extends AbstractBaseDomain implements Serializable {
    private static final long serialVersionUID = 4954216018177391110L;

    private String redTicketDataSerialNumber;//序列号
    private BigDecimal goodsAmount;//金额
    private BigDecimal goodsPrice;//单价
    private String goodsName;// 货物或应税劳务名称
    private Integer goodsNumber;//数量

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getRedTicketDataSerialNumber() {
        return redTicketDataSerialNumber;
    }

    public void setRedTicketDataSerialNumber(String redTicketDataSerialNumber) {
        this.redTicketDataSerialNumber = redTicketDataSerialNumber;
    }

    public BigDecimal getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(Integer goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
