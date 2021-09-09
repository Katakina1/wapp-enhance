package com.xforceplus.wapp.modules.report.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票认证日报或月报实体
 */
@Getter
@Setter
public class DailyReportEntity implements Serializable {

    public DailyReportEntity(){}

    public DailyReportEntity(String rzhDate){
        this.rzhDate = rzhDate;
        this.count = 0;
        this.amount = 0.00;
        this.tax = 0.00;
    }

    public String getRzhDate() {
		return rzhDate;
	}

	public void setRzhDate(String rzhDate) {
		this.rzhDate = rzhDate;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	//认证日期(或税款所属期),进项税额报表中为转出原因类别
    private String rzhDate;

    //发票数量
    private Integer count;

    //合计金额
    private Double amount;

    //合计税额
    private Double tax;
}
