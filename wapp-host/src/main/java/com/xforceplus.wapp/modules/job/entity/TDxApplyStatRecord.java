package com.xforceplus.wapp.modules.job.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;



import lombok.Data;

@Data

public class TDxApplyStatRecord implements Serializable {

    private Long id;

    private String batchNo;

    private String taxno;

    private String applyType;

    private String resultStatus;

    private Date createDate;

    private Date updateDate;
    
    private String skssq;
    
    private String interfaceType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo == null ? null : batchNo.trim();
    }

   
    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus == null ? null : resultStatus.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

   
}