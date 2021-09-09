package com.xforceplus.wapp.modules.base.entity;

public class CostEntity {
    private Integer id;
    private Integer orgid;
    private String costcode;
    private String costname;


    public Integer getOrgid() {
        return orgid;
    }

    public void setOrgid(Integer orgid) {
        this.orgid = orgid;
    }

    public String getCostcode() {
        return costcode;
    }

    public void setCostcode(String costcode) {
        this.costcode = costcode;
    }

    public String getCostname() {
        return costname;
    }

    public void setCostname(String costname) {
        this.costname = costname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
