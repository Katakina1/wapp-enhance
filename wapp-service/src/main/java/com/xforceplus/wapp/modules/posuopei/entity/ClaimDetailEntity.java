package com.xforceplus.wapp.modules.posuopei.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ClaimDetailEntity extends BaseEntity implements Serializable {


    private  Integer id;
    /**
     *
     */
    private  String storeNbr;

    //
    private Date finalDate;
    /**
     *
     */
    private  String returnGoodsCode;
    /**
     *
     */
    private String vndrNbr;
    /**
     *
     */
    private String itemNbr;
    /**
     *
     */
    private String upcNbr;

    /**
     *
     */
    private  String deptNbr;

    /**
     *
     */
    private  String goodsName;

    private  String vendorStockId;

    /**
     *
     */
    private  BigDecimal vnpkCost;

    /**
     *
     */
    private  int vnpkQty;


    /**
     *
     */
    private  BigDecimal goodsPrice;

    /**
     *
     */
    private  int goodsNumber;

    /**
     *
     */
    private  BigDecimal goodsAmount;

    private String taxRate;

    private String gategoryNbr;

    public ClaimDetailEntity(String storeNbr, Date finalDate, String returnGoodsCode, String vndrNbr, String itemNbr, String upcNbr, String deptNbr, String goodsName, String vendorStockId, BigDecimal vnpkCost, int vnpkQty, BigDecimal goodsPrice, int goodsNumber, BigDecimal goodsAmount, String taxRate, String gategoryNbr) {
        this.storeNbr = storeNbr;
        this.finalDate = finalDate;
        this.returnGoodsCode = returnGoodsCode;
        this.vndrNbr = vndrNbr;
        this.itemNbr = itemNbr;
        this.upcNbr = upcNbr;
        this.deptNbr = deptNbr;
        this.goodsName = goodsName;
        this.vendorStockId = vendorStockId;
        this.vnpkCost = vnpkCost;
        this.vnpkQty = vnpkQty;
        this.goodsPrice = goodsPrice;
        this.goodsNumber = goodsNumber;
        this.goodsAmount = goodsAmount;
        this.taxRate = taxRate;
        this.gategoryNbr = gategoryNbr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStoreNbr() {
        return storeNbr;
    }

    public void setStoreNbr(String storeNbr) {
        this.storeNbr = storeNbr;
    }

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    public String getReturnGoodsCode() {
        return returnGoodsCode;
    }

    public void setReturnGoodsCode(String returnGoodsCode) {
        this.returnGoodsCode = returnGoodsCode;
    }

    public String getVndrNbr() {
        return vndrNbr;
    }

    public void setVndrNbr(String vndrNbr) {
        this.vndrNbr = vndrNbr;
    }

    public String getItemNbr() {
        return itemNbr;
    }

    public void setItemNbr(String itemNbr) {
        this.itemNbr = itemNbr;
    }

    public String getUpcNbr() {
        return upcNbr;
    }

    public void setUpcNbr(String upcNbr) {
        this.upcNbr = upcNbr;
    }

    public String getDeptNbr() {
        return deptNbr;
    }

    public void setDeptNbr(String deptNbr) {
        this.deptNbr = deptNbr;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getVendorStockId() {
        return vendorStockId;
    }

    public void setVendorStockId(String vendorStockId) {
        this.vendorStockId = vendorStockId;
    }

    public BigDecimal getVnpkCost() {
        return vnpkCost;
    }

    public void setVnpkCost(BigDecimal vnpkCost) {
        this.vnpkCost = vnpkCost;
    }

    public int getVnpkQty() {
        return vnpkQty;
    }

    public void setVnpkQty(int vnpkQty) {
        this.vnpkQty = vnpkQty;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public int getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
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

    public String getGategoryNbr() {
        return gategoryNbr;
    }

    public void setGategoryNbr(String gategoryNbr) {
        this.gategoryNbr = gategoryNbr;
    }
}
