package com.xforceplus.wapp.modules.businessData.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Adil.Xu
 */
public class AgreementEntity extends BaseEntity implements Serializable{
    private static final long serialVersionUID = -4468433059820727270L;
    //ID
    private Long id;

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
    private String redticketDataSerialNumber;

    //说明
    private String description;

    public void setId(Long id) { this.id = id; }

    public void setAgreementCode(String agreementCode) { this.agreementCode = agreementCode; }

    public void setSupplierAssociation(String supplierAssociation) { this.supplierAssociation = supplierAssociation; }

    public void setAgreementDate(Date agreementDate) { this.agreementDate = agreementDate; }

    public void setAgreementAmount(BigDecimal agreementAmount) { this.agreementAmount = agreementAmount; }

    public void setAgreementCostAmount(BigDecimal agreementCostAmount) { this.agreementCostAmount = agreementCostAmount; }

    public void setAgreementStatus(String agreementStatus) { this.agreementStatus = agreementStatus; }

    public void setRedticketDataSerialNumber(String redticketDataSerialNumber) { this.redticketDataSerialNumber = redticketDataSerialNumber; }

    public Long getId() { return id; }

    public String getAgreementCode() { return agreementCode; }

    public String getSupplierAssociation() { return supplierAssociation; }

    public Date getAgreementDate() { return agreementDate; }

    public BigDecimal getAgreementAmount() { return agreementAmount; }

    public BigDecimal getAgreementCostAmount() { return agreementCostAmount; }

    public String getAgreementStatus() { return agreementStatus; }

    public String getRedticketDataSerialNumber() { return redticketDataSerialNumber; }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
