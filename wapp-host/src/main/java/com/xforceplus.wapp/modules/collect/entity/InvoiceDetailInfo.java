package com.xforceplus.wapp.modules.collect.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 未补发票明细
 * @author Colin.hu
 * @date 4/12/2018
 */
@Getter @Setter @ToString
public class InvoiceDetailInfo extends AbstractBaseDomain {

    private static final long serialVersionUID = 2463915817574186083L;

    private String taxAmount;//税额
    private String goodsName;//货物或应税劳务名称
    private String invoiceNo;//发票号码
    public String getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getDetailNo() {
		return detailNo;
	}

	public void setDetailNo(String detailNo) {
		this.detailNo = detailNo;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getLx() {
		return lx;
	}

	public void setLx(String lx) {
		this.lx = lx;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTxrqz() {
		return txrqz;
	}

	public void setTxrqz(String txrqz) {
		this.txrqz = txrqz;
	}

	public String getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDetailAmount() {
		return detailAmount;
	}

	public void setDetailAmount(String detailAmount) {
		this.detailAmount = detailAmount;
	}

	public String getTxrqq() {
		return txrqq;
	}

	public void setTxrqq(String txrqq) {
		this.txrqq = txrqq;
	}

	public String getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(String goodsNum) {
		this.goodsNum = goodsNum;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getCph() {
		return cph;
	}

	public void setCph(String cph) {
		this.cph = cph;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String num;//数量
    private String detailNo;//明细序号
    private String unitPrice;//单价
    private String lx;//类型
    private String uuid;//唯一标识(发票代码+发票号码)
    private String txrqz;//通行日期止
    private String taxRate;//税率
    private String invoiceCode;//发票代码
    private String unit;//单位
    private String detailAmount;//金额
    private String txrqq;//通行日期起
    private String goodsNum;//商品编码
    private String model;//规格型号
    private String cph;//车牌号
    private  String category1;

	public String getCategory1() {
		return category1;
	}

	public void setCategory1(String category1) {
		this.category1 = category1;
	}

	@Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
