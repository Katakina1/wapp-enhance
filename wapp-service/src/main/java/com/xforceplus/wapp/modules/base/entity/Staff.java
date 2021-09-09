package com.xforceplus.wapp.modules.base.entity;

import java.util.List;

public class Staff {
    private int id;
    private String staffNo;
    private String winID;
    private String staffName;
    private String email;
    private String costCenter;
    private String costCenterName;
    private String vendors;
    private String gfTaxNo;
    private String jvs;
    private List<Integer> orgids;
    private Integer orgid;
    private String samAd;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public String getWinID() {
        return winID;
    }

    public void setWinID(String winID) {
        this.winID = winID;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getCostCenterName() {
        return costCenterName;
    }

    public void setCostCenterName(String costCenterName) {
        this.costCenterName = costCenterName;
    }

    public String getVendors() {
        return vendors;
    }

    public void setVendors(String vendors) {
        this.vendors = vendors;
    }

    public String getGfTaxNo() {
        return gfTaxNo;
    }

    public void setGfTaxNo(String gfTaxNo) {
        this.gfTaxNo = gfTaxNo;
    }

    public String getJvs() {
        return jvs;
    }

    public void setJvs(String jvs) {
        this.jvs = jvs;
    }


    public List<Integer> getOrgids() {
        return orgids;
    }

    public void setOrgids(List<Integer> orgids) {
        this.orgids = orgids;
    }

    public Integer getOrgid() {
        return orgid;
    }

    public void setOrgid(Integer orgid) {
        this.orgid = orgid;
    }

    public String getSamAd() {
        return samAd;
    }

    public void setSamAd(String samAd) {
        this.samAd = samAd;
    }
}
