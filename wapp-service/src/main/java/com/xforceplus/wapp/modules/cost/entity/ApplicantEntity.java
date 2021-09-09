package com.xforceplus.wapp.modules.cost.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;

public class ApplicantEntity extends AbstractBaseDomain {

    private int  applicantId;
    private String epsNo;
    private String shopNo;
    private String applicantDepartment;
    private String applicantNo;
    private String applicantName;
    private String applicantCall;
    private String applicantSubarea;
    private String importDate;
    private int[] ids;

    public int getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(int applicantId) {
        this.applicantId = applicantId;
    }

    public String getEpsNo() {
        return epsNo;
    }

    public void setEpsNo(String epsNo) {
        this.epsNo = epsNo;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getApplicantDepartment() {
        return applicantDepartment;
    }

    public void setApplicantDepartment(String applicantDepartment) {
        this.applicantDepartment = applicantDepartment;
    }

    public String getApplicantNo() {
        return applicantNo;
    }

    public void setApplicantNo(String applicantNo) {
        this.applicantNo = applicantNo;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantCall() {
        return applicantCall;
    }

    public void setApplicantCall(String applicantCall) {
        this.applicantCall = applicantCall;
    }

    public String getApplicantSubarea() {
        return applicantSubarea;
    }

    public void setApplicantSubarea(String applicantSubarea) {
        this.applicantSubarea = applicantSubarea;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }


}
