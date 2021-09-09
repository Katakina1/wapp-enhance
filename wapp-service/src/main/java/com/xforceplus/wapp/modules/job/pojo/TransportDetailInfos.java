package com.xforceplus.wapp.modules.job.pojo;

public class TransportDetailInfos extends BasePojo{

	private static final long serialVersionUID = 3867863599745168884L;

	private String detailNo;
	
	private String costItem ;
	
	private String costAmount ;

	public String getDetailNo() {
		return detailNo;
	}

	public void setDetailNo(String detailNo) {
		this.detailNo = detailNo;
	}

	public String getCostItem() {
		return costItem;
	}

	public void setCostItem(String costItem) {
		this.costItem = costItem;
	}

	public String getCostAmount() {
		return costAmount;
	}

	public void setCostAmount(String costAmount) {
		this.costAmount = costAmount;
	}
	
	
	
}
