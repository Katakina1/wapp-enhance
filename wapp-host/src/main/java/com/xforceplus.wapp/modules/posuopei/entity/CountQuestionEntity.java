package com.xforceplus.wapp.modules.posuopei.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author raymond.yan
 */
public class CountQuestionEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -5434052995174114551L;
    private  Integer id;
    private String poCode;
    private String receiptid;
    private String goodsNo;
    private BigDecimal systemAmount;
    private Integer vendorNumber;
    private Integer systemNumber;
    private Integer numberDifference;
    private BigDecimal amountDifference;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(Integer vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }



    public String getReceiptid() {
        return receiptid;
    }

    public void setReceiptid(String receiptid) {
        this.receiptid = receiptid;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public BigDecimal getSystemAmount() {
        return systemAmount;
    }

    public void setSystemAmount(BigDecimal systemAmount) {
        this.systemAmount = systemAmount;
    }



    public Integer getSystemNumber() {
        return systemNumber;
    }

    public void setSystemNumber(Integer systemNumber) {
        this.systemNumber = systemNumber;
    }

    public Integer getNumberDifference() {
        return numberDifference;
    }

    public void setNumberDifference(Integer numberDifference) {
        this.numberDifference = numberDifference;
    }

    public BigDecimal getAmountDifference() {
        return amountDifference;
    }

    public void setAmountDifference(BigDecimal amountDifference) {
        this.amountDifference = amountDifference;
    }
}
