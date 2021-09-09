package com.xforceplus.wapp.modules.job.pojo.question;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author raymond.yan
 */
public class QuestionDetail implements Serializable {
    private static final long serialVersionUID = 9018442101703562443L;
    private  Integer id;
    private String poCode;
    private String claimno;
    private String goodsNo;
    private BigDecimal systemAmount;
    private BigDecimal vendorAmount;
    private Integer numbers;
    private BigDecimal differenceAmount;
    private Integer vendorNumber;
    private Integer systemNumber;
    private Integer numberDifference;
    private String receiptid;

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

    public Integer getNumbers() {
        return numbers;
    }

    public void setNumbers(Integer numbers) {
        this.numbers = numbers;
    }

    public String getClaimno() {
        return claimno;
    }

    public void setClaimno(String claimno) {
        this.claimno = claimno;
    }

    public BigDecimal getDifferenceAmount() {
        return differenceAmount;
    }

    public void setDifferenceAmount(BigDecimal differenceAmount) {
        this.differenceAmount = differenceAmount;
    }

    public Integer getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(Integer vendorNumber) {
        this.vendorNumber = vendorNumber;
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

    public String getReceiptid() {
        return receiptid;
    }

    public void setReceiptid(String receiptid) {
        this.receiptid = receiptid;
    }
}
