package com.xforceplus.wapp.modules.report.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ReportStatisticsEntity implements Serializable {

    public ReportStatisticsEntity(){
        this.totalCount=0;
        this.totalAmount=0.00;
        this.totalTax=0.00;
        this.pcTotalAmount=0.00;
        this.mdTotalAmount=0.00;
    }

    //合计数量
    private int totalCount;

    //合计金额
    private Double totalAmount;

    //合计税额
    private Double totalTax;

    //合计红冲数量
    private int redPushTotalCount;
    //合计红冲金额
    private Double redPushTotalAmount;
    //合计税额
    private Double redPushTotalTaxAmount;
    //合计价税合计
    private Double taxAmount;

    //债务数据PC合计金额
    private Double pcTotalAmount;
    //债务数据md合计金额
    private Double mdTotalAmount;

    public Double getPcTotalAmount() {
        return pcTotalAmount;
    }

    public void setPcTotalAmount(Double pcTotalAmount) {
        this.pcTotalAmount = pcTotalAmount;
    }

    public Double getMdTotalAmount() {
        return mdTotalAmount;
    }

    public void setMdTotalAmount(Double mdTotalAmount) {
        this.mdTotalAmount = mdTotalAmount;
    }

    public int getRedPushTotalCount() {
        return redPushTotalCount;
    }

    public void setRedPushTotalCount(int redPushTotalCount) {
        this.redPushTotalCount = redPushTotalCount;
    }

    public Double getRedPushTotalAmount() {
        return redPushTotalAmount;
    }

    public void setRedPushTotalAmount(Double redPushTotalAmount) {
        this.redPushTotalAmount = redPushTotalAmount;
    }

    public Double getRedPushTotalTaxAmount() {
        return redPushTotalTaxAmount;
    }

    public void setRedPushTotalTaxAmount(Double redPushTotalTaxAmount) {
        this.redPushTotalTaxAmount = redPushTotalTaxAmount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(Double totalTax) {
        this.totalTax = totalTax;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }
}
