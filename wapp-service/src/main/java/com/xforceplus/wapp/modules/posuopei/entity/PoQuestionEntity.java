package com.xforceplus.wapp.modules.posuopei.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author raymond.yan
 */
public class PoQuestionEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 9018442101703562443L;
    private  Integer id;
    private String poCode;
    private String goodsNo;
    private BigDecimal systemAmount;
    private BigDecimal vendorAmount;
    private Integer number;
    private BigDecimal difference;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
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

    public BigDecimal getVendorAmount() {
        return vendorAmount;
    }

    public void setVendorAmount(BigDecimal vendorAmount) {
        this.vendorAmount = vendorAmount;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }
}
