package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;

public class ProtocolDetailEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = 5813174958042599586L;
    private String venderId;//供应商号
    private String protocolNo;//协议号
    private String reason;//扣款原因
    private String number;//号码
    private String numberDesc;//号码解释
    private BigDecimal detailAmount;//金额
    private String store;//店号

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
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

    public BigDecimal getDetailAmount() {
        return detailAmount;
    }

    public void setDetailAmount(BigDecimal detailAmount) {
        this.detailAmount = detailAmount;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
