package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;
import java.util.Date;

public class ProtocolEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = -224142382920525661L;
    private Long id;//ID
    private String protocolNo;//协议号
    private String venderId;//供应商号
    private String deptNo;//部门号
    private String payItem;//扣款项目
    private String payCompanyCode;//扣款公司供码
    private BigDecimal amount;//金额
    private String protocolStatus;//协议状态
    private Date caseDate;//定案日期
    private String seq;//序号
    private Date payDate;//付款日期
    private String redticketDataSerialNumber;//红票序列号

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

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getPayItem() {
        return payItem;
    }

    public void setPayItem(String payItem) {
        this.payItem = payItem;
    }

    public String getPayCompanyCode() {
        return payCompanyCode;
    }

    public void setPayCompanyCode(String payCompanyCode) {
        this.payCompanyCode = payCompanyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getProtocolStatus() {
        return protocolStatus;
    }

    public void setProtocolStatus(String protocolStatus) {
        this.protocolStatus = protocolStatus;
    }

    public Date getCaseDate() {
        return caseDate;
    }

    public void setCaseDate(Date caseDate) {
        this.caseDate = caseDate;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getRedticketDataSerialNumber() {
        return redticketDataSerialNumber;
    }

    public void setRedticketDataSerialNumber(String redticketDataSerialNumber) {
        this.redticketDataSerialNumber = redticketDataSerialNumber;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
