package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class AgreementEntity extends AbstractBaseDomain implements Serializable{
    private static final long serialVersionUID = 4954216018177391110L;


    //协议号
    private String agreementCode;

    //供应商号
    private String supplierAssociation;

    //协议时间
    private Date agreementDate;

    //协议金额
    private BigDecimal agreementAmount;

    //协议成本金额
    private BigDecimal agreementCostAmount;

    //协议状态
    private String agreementStatus;

    //如果被红冲，产生序列号
    private String redTicketDataSerialNumber;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    public String getAgreementCode() {
        return agreementCode;
    }

    public void setAgreementCode(String agreementCode) {
        this.agreementCode = agreementCode;
    }

    public String getSupplierAssociation() {
        return supplierAssociation;
    }

    public void setSupplierAssociation(String supplierAssociation) {
        this.supplierAssociation = supplierAssociation;
    }

    public Date getAgreementDate() {
        return agreementDate;
    }

    public void setAgreementDate(Date agreementDate) {
        this.agreementDate = agreementDate;
    }

    public BigDecimal getAgreementAmount() {
        return agreementAmount;
    }

    public void setAgreementAmount(BigDecimal agreementAmount) {
        this.agreementAmount = agreementAmount;
    }

    public BigDecimal getAgreementCostAmount() {
        return agreementCostAmount;
    }

    public void setAgreementCostAmount(BigDecimal agreementCostAmount) {
        this.agreementCostAmount = agreementCostAmount;
    }

    public String getAgreementStatus() {
        return agreementStatus;
    }

    public void setAgreementStatus(String agreementStatus) {
        this.agreementStatus = agreementStatus;
    }

    public String getRedTicketDataSerialNumber() {
        return redTicketDataSerialNumber;
    }

    public void setRedTicketDataSerialNumber(String redTicketDataSerialNumber) {
        this.redTicketDataSerialNumber = redTicketDataSerialNumber;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
