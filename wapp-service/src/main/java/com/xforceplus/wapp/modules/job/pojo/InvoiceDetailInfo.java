package com.xforceplus.wapp.modules.job.pojo;

public class InvoiceDetailInfo extends BasePojo{

	private static final long serialVersionUID = 3867863599745168884L;

	private String detailNo;
	
	private String goodsName;
	
	private String specificationModel;
	
	private String unit;
	
	private String num;
	
	private String unitPrice;
	
	private String cph;
	private String lx;
	private String txrqq;
	private String txrqz;

	private String detailAmount;
	
	private String taxRate;
	
	private String taxAmount;
	
	private String goodsNum;

	private String  taxCodeName;
	
	public String getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(String goodsNum) {
		this.goodsNum = goodsNum;
	}

	public String getDetailNo() {
		return detailNo;
	}

	public void setDetailNo(String detailNo) {
		this.detailNo = detailNo;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
	public String getSpecificationModel() {
		return specificationModel;
	}

	public void setSpecificationModel(String specificationModel) {
		this.specificationModel = specificationModel;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getDetailAmount() {
		return detailAmount;
	}

	public void setDetailAmount(String detailAmount) {
		this.detailAmount = detailAmount;
	}

	public String getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}

	public String getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getCph() {
		return cph;
	}

	public void setCph(String cph) {
		this.cph = cph;
	}

	public String getLx() {
		return lx;
	}

	public void setLx(String lx) {
		this.lx = lx;
	}

	public String getTxrqq() {
		return txrqq;
	}

	public void setTxrqq(String txrqq) {
		this.txrqq = txrqq;
	}

	public String getTxrqz() {
		return txrqz;
	}

	public void setTxrqz(String txrqz) {
		this.txrqz = txrqz;
	}

	public String getTaxCodeName() {
		return taxCodeName;
	}

	public void setTaxCodeName(String taxCodeName) {
		this.taxCodeName = taxCodeName;
	}
}
