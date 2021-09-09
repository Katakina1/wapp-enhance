package com.xforceplus.wapp.modules.businessData.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 索赔明细表
 * @author Adil.Xu
 */
public class ReturngoodsdetEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 3367762505843373141L;
    //ID
    private Long id;
    //退货号（关联退货表）
    private String returnGoodsCode;
    //货物名称
    private String goodsName;
    //规格型号
    private String goodsModel;
    //货物单价
    private BigDecimal goodsPrice;
    //货物单位
    private String goodsUnit;
    //货物数量
    private Integer goodsNumber;
    //货物金额
    private BigDecimal goodsAmount;
    //税额
    private BigDecimal taxAmount;
    //税率
    private String taxRate;
    //门店号
    private String storeNbr;
    //商品编码
    private String itemNbr;
    //商品条码
    private String upcNbr;
    //部门号
    private String deptNbr;
    //定案日期
    private Date finalDate;
    //供应商库存id
    private String vendorStockId;
    //数量
    private Integer vnpkQty;
    //类别码
    private String gategoryNbr;
    //供应商号
    private String vndrNbr;
    //单个成本
    private BigDecimal vnpkCost;

    public void setId(Long id) { this.id = id; }

    public void setReturnGoodsCode(String returnGoodsCode) { this.returnGoodsCode = returnGoodsCode; }

    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }

    public void setGoodsModel(String goodsModel) { this.goodsModel = goodsModel; }

    public void setGoodsPrice(BigDecimal goodsPrice) { this.goodsPrice = goodsPrice; }

    public void setGoodsUnit(String goodsUnit) { this.goodsUnit = goodsUnit; }

    public void setGoodsNumber(Integer goodsNumber) { this.goodsNumber = goodsNumber; }

    public void setGoodsAmount(BigDecimal goodsAmount) { this.goodsAmount = goodsAmount;}

    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public void setTaxRate(String taxRate) { this.taxRate = taxRate; }

    public Long getId() { return id; }

    public String getReturnGoodsCode() { return returnGoodsCode;}

    public String getGoodsName() { return goodsName; }

    public String getGoodsModel() { return goodsModel; }

    public BigDecimal getGoodsPrice() { return goodsPrice; }

    public String getGoodsUnit() { return goodsUnit; }

    public Integer getGoodsNumber() { return goodsNumber; }

    public BigDecimal getGoodsAmount() { return goodsAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }

    public String getTaxRate() { return taxRate; }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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
}
