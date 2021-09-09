package com.xforceplus.wapp.modules.job.pojo;

import java.util.Date;

/**
 * 
 * @Title TaxCurrent.java
 * @Description 企业税款所属期
 * @author X Yang
 * @date  
 */
public class TaxCurrent extends BasePojo{

	private String curTaxno;
	private String compName;
	private String incomeMoth;
	private String selectStartDate;
	private String selectEndDate;
	private String incomeEndDate;
	private String oldTaxno;
	private String compLevel;
	private String appPeriod;


	public String getCurTaxno() {
		return curTaxno;
	}

	public void setCurTaxno(String curTaxno) {
		this.curTaxno = curTaxno;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getIncomeMoth() {
		return incomeMoth;
	}

	public void setIncomeMoth(String incomeMoth) {
		this.incomeMoth = incomeMoth;
	}

	public String getSelectStartDate() {
		return selectStartDate;
	}

	public void setSelectStartDate(String selectStartDate) {
		this.selectStartDate = selectStartDate;
	}

	public String getSelectEndDate() {
		return selectEndDate;
	}

	public void setSelectEndDate(String selectEndDate) {
		this.selectEndDate = selectEndDate;
	}

	public String getIncomeEndDate() {
		return incomeEndDate;
	}

	public void setIncomeEndDate(String incomeEndDate) {
		this.incomeEndDate = incomeEndDate;
	}

	public String getOldTaxno() {
		return oldTaxno;
	}

	public void setOldTaxno(String oldTaxno) {
		this.oldTaxno = oldTaxno;
	}

	public String getCompLevel() {
		return compLevel;
	}

	public void setCompLevel(String compLevel) {
		this.compLevel = compLevel;
	}

	public String getAppPeriod() {
		return appPeriod;
	}

	public void setAppPeriod(String appPeriod) {
		this.appPeriod = appPeriod;
	}
}
