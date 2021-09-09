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

	//合计数量
    private int totalCount;

    //合计金额
    private Double totalAmount;

    //合计税额
    private Double totalTax;
}
