package com.xforceplus.wapp.modules.index.entity;

/**
 * @author Bobby
 * @date 2018/4/16
 * 首页-今日采集
 */

public final class IndexInvoiceScanMatchCountModel {


    /**
     * 统计值
     */
    private Integer countNum;

    /**
     * 扫描匹配状态
     */
    private String scanMatchStatus;

    public Integer getCountNum() {
        return countNum;
    }

    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    public String getScanMatchStatus() {
        return scanMatchStatus;
    }

    public void setScanMatchStatus(String scanMatchStatus) {
        this.scanMatchStatus = scanMatchStatus;
    }
}
