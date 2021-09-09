package com.xforceplus.wapp.modules.protocol.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;
import java.util.Date;

public class ProtocolDetailEntity extends AbstractBaseDomain {

    //协议号码
    private String protocolNo;

    //供应商号
    private String venderId;

    //原因
    private String reason;

    //号码
    private String number;

    //号码解释
    private String numberDesc;

    //店号
    private String store;

    //金额
    private BigDecimal detailAmount;

    //协议主信息金额
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCaseDate() {
        return caseDate;
    }

    public void setCaseDate(Date caseDate) {
        this.caseDate = caseDate;
    }

    //定案日期
    private Date caseDate;

    public String getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumberDesc() {
        return numberDesc;
    }

    public void setNumberDesc(String numberDesc) {
        this.numberDesc = numberDesc;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public BigDecimal getDetailAmount() {
        return detailAmount;
    }

    public void setDetailAmount(BigDecimal detailAmount) {
        this.detailAmount = detailAmount;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
