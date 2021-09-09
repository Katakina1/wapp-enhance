package com.xforceplus.wapp.modules.job.pojo.vendorMaster;

import java.io.Serializable;
import java.util.Date;

public class VendorLog implements Serializable{
    private Long logId;

    private String status;

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }

    public String getReqText() {
        return reqText;
    }

    public void setReqText(String reqText) {
        this.reqText = reqText;
    }

    public Date getResDate() {
        return resDate;
    }

    public void setResDate(Date resDate) {
        this.resDate = resDate;
    }

    public String getResText() {
        return resText;
    }

    public void setResText(String resText) {
        this.resText = resText;
    }

    private Date reqDate;

    private String reqText;

    private Date resDate;

    private String resText;

    private String total;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}