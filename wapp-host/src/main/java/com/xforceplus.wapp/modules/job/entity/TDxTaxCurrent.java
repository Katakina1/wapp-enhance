package com.xforceplus.wapp.modules.job.entity;

import java.io.Serializable;
import java.util.Date;

public class TDxTaxCurrent implements Serializable{
    private Long id;

    private String taxno;

    private String taxname;

    private String currentTaxPeriod;

    private Date selectStartDate;

    private Date selectEndDate;

    private Date operationEndDate;

    private String oldTaxNo;

    private String declarePeriod;

    private String creditRating;

    private Date updateTime;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaxno() {
        return taxno;
    }

    public void setTaxno(String taxno) {
        this.taxno = taxno == null ? null : taxno.trim();
    }

    public String getTaxname() {
        return taxname;
    }

    public void setTaxname(String taxname) {
        this.taxname = taxname == null ? null : taxname.trim();
    }

    public String getCurrentTaxPeriod() {
        return currentTaxPeriod;
    }

    public void setCurrentTaxPeriod(String currentTaxPeriod) {
        this.currentTaxPeriod = currentTaxPeriod == null ? null : currentTaxPeriod.trim();
    }

    public Date getSelectStartDate() {
        return selectStartDate;
    }

    public void setSelectStartDate(Date selectStartDate) {
        this.selectStartDate = selectStartDate;
    }

    public Date getSelectEndDate() {
        return selectEndDate;
    }

    public void setSelectEndDate(Date selectEndDate) {
        this.selectEndDate = selectEndDate;
    }

    public Date getOperationEndDate() {
        return operationEndDate;
    }

    public void setOperationEndDate(Date operationEndDate) {
        this.operationEndDate = operationEndDate;
    }

    public String getOldTaxNo() {
        return oldTaxNo;
    }

    public void setOldTaxNo(String oldTaxNo) {
        this.oldTaxNo = oldTaxNo == null ? null : oldTaxNo.trim();
    }

    public String getDeclarePeriod() {
        return declarePeriod;
    }

    public void setDeclarePeriod(String declarePeriod) {
        this.declarePeriod = declarePeriod == null ? null : declarePeriod.trim();
    }

    public String getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating == null ? null : creditRating.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}