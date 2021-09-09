package com.xforceplus.wapp.modules.collect.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 统计发票各个税率的税额、金额实体
 * @author Colin.hu
 * @date 4/18/2018
 */
@Getter @Setter @ToString
public class RecordInvoiceStatistics extends AbstractBaseDomain {

    private static final long serialVersionUID = -541980853663321623L;

    private Double taxAmount;//税额
    private Double zkbl;//扣折比率
    private String invoiceNo;//发票号码
    private String jylx;//易交类型
    private Double taxRate;//税率
    private String invoiceCode;//发票代码
    public Double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public Double getZkbl() {
		return zkbl;
	}

	public void setZkbl(Double zkbl) {
		this.zkbl = zkbl;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getJylx() {
		return jylx;
	}

	public void setJylx(String jylx) {
		this.jylx = jylx;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public Double getDetailAmount() {
		return detailAmount;
	}

	public void setDetailAmount(Double detailAmount) {
		this.detailAmount = detailAmount;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getZkje() {
		return zkje;
	}

	public void setZkje(Double zkje) {
		this.zkje = zkje;
	}

	public String getYwzk() {
		return ywzk;
	}

	public void setYwzk(String ywzk) {
		this.ywzk = ywzk;
	}

	public java.util.Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}

	public String getDepart() {
		return depart;
	}

	public void setDepart(String depart) {
		this.depart = depart;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private Double detailAmount;//金额
    private Double totalAmount;//价税合计
    private Double zkje;//折扣金额
    private String ywzk;//无有折扣
    private java.util.Date createDate;//创建时间
    private String depart;//门部

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
