package com.xforceplus.wapp.modules.InformationInquiry.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 索赔表
 */
public class ClaimDetailEntity implements Serializable {

    private Long id;
    private String claimno;//索赔号
    private String goodsName;//货物名称
    private String goodsModel;//规格型号
    private BigDecimal goodsPrice;//单价
    private String goodsUnit;//单位
    private Integer goodsNumber;//商品数量
    private BigDecimal goodsAmount;//金额
    private BigDecimal taxAmount;//税额
    private String taxRate;//税率
    private String storeNbr;//门店号
    private String itemNbr;//商品编码
    private String upcNbr;//商品条码
    private String deptNbr;//部门号
    private Date finalDate;//定案日期
    private String vendorStockId;//商品库存id
    private Integer vnpkQty;//数量
    private String gategoryNbr;//类别码
    private String vndrNbr;//供应商号
    private BigDecimal vnpkCost;//单个成本

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
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

    public BigDecimal getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getStoreNbr() {
        return storeNbr;
    }

    public void setStoreNbr(String storeNbr) {
        this.storeNbr = storeNbr;
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

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    public String getVendorStockId() {
        return vendorStockId;
    }

    public void setVendorStockId(String vendorStockId) {
        this.vendorStockId = vendorStockId;
    }

    public Integer getVnpkQty() {
        return vnpkQty;
    }

    public void setVnpkQty(Integer vnpkQty) {
        this.vnpkQty = vnpkQty;
    }

    public String getGategoryNbr() {
        return gategoryNbr;
    }

    public void setGategoryNbr(String gategoryNbr) {
        this.gategoryNbr = gategoryNbr;
    }

    public String getVndrNbr() {
        return vndrNbr;
    }

    public void setVndrNbr(String vndrNbr) {
        this.vndrNbr = vndrNbr;
    }

    public BigDecimal getVnpkCost() {
        return vnpkCost;
    }

    public void setVnpkCost(BigDecimal vnpkCost) {
        this.vnpkCost = vnpkCost;
    }

    public String getClaimno() {
        return claimno;
    }

    public void setClaimno(String claimno) {
        this.claimno = claimno;
    }
}
