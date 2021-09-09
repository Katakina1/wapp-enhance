package com.xforceplus.wapp.modules.posuopei.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

/**
 * 导入页面实体
 * @author Raymond.yan
 * @date 10/20/2018
 */
public class AddClaimEntity extends AbstractBaseDomain {


    private static final long serialVersionUID = 3693528970374359711L;


    /**
     * 订单号
     */
    private String poCode;

    /**
     * 索赔号
     */
    private String claimno;

    /**
     * 收货号
     */
    private String receiptid;

    /**
     * 商品号
     */
    private String goodsNo;

    /**
     * 系统单价
     */
    private String systemAmount;
    /**
     * 供应商单价
     */
    private String vendorAmount;

    /**
     * 数量
     */
    private String number;
    /**
     * 供应商数量
     */
    private String vendorNumber;
    /**
     * 系统数量
     */
    private String systemNumber;
    /**
     * 收退货金额差额
     */
    private String amountDifference;

    /**
     * 数量差异
     */
    private String numberDifference;
    /**
     * 金额差额
     */

    private  String difference;

    /**
     * excel导入的序号
     */
    private int indexNo;

    private Boolean isEmpty;
    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public String getClaimno() {
        return claimno;
    }

    public void setClaimno(String claimno) {
        this.claimno = claimno;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getSystemAmount() {
        return systemAmount;
    }

    public void setSystemAmount(String systemAmount) {
        this.systemAmount = systemAmount;
    }

    public String getVendorAmount() {
        return vendorAmount;
    }

    public void setVendorAmount(String vendorAmount) {
        this.vendorAmount = vendorAmount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDifference() {
        return difference;
    }

    public void setDifference(String difference) {
        this.difference = difference;
    }

    public int getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(int indexNo) {
        this.indexNo = indexNo;
    }

    public Boolean getEmpty() {
        return isEmpty;
    }

    public void setEmpty(Boolean empty) {
        isEmpty = empty;
    }
    public String getReceiptid() {
        return receiptid;
    }

    public String getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public String getSystemNumber() {
        return systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
    }

    public String getAmountDifference() {
        return amountDifference;
    }

    public void setAmountDifference(String amountDifference) {
        this.amountDifference = amountDifference;
    }

    public void setReceiptid(String receiptid) {
        this.receiptid = receiptid;
    }

    public String getNumberDifference() {
        return numberDifference;
    }

    public void setNumberDifference(String numberDifference) {
        this.numberDifference = numberDifference;
    }






    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
