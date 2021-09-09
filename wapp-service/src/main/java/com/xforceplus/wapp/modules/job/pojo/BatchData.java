package com.xforceplus.wapp.modules.job.pojo;

import java.util.Date;

public class BatchData {
    private String batchNo;

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    @Override
    public String toString() {
        return "BatchData{" +
                "batchNo='" + batchNo + '\'' +
                '}';
    }
}