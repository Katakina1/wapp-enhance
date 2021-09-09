package com.xforceplus.wapp.modules.signin.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Date;

/** 
 * <p>Title:  RecordInvoiceDetail</p>
 * <p>Description: 电子底账明细</p> 
 * @author yuanlz
 * @date 2016年12月21日 上午10:26:24 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordInvoiceDetail  {

	private static final long serialVersionUID = -422068344392372903L;
	
	/**
	 * 主键
	 */
	@JsonIgnore
	private Long id;
	
	/**
	 * 发票代码
	 */
	@JsonIgnore
	private String invoiceCode;
	
	/**
	 * 发票号码
	 */
	@JsonIgnore
	private String invoiceNo;
	
	/**
	 * 明细序号
	 */
	private Integer detailNo;
	
	/**
	 * 货物名称
	 */
	private String goodName;
	
	/**
	 * 规格型号
	 */
	private String model;
	
	/**
	 * 单位
	 */
	private String unit;
	
	/**
	 * 数量
	 */
	private BigDecimal num;
	
	/**
	 * 单价
	 */
	private BigDecimal unitPrice;
	
	/**
	 * 金额
	 */
	private BigDecimal detailAmount;
	
	/**
	 * 税率
	 */
	private BigDecimal taxRate;
	
	/**
	 * 税额
	 */
	private BigDecimal taxAmount;
	/**
	 * 最后一次修改时间
	 */
	private Date updateDate;

	/**
	 * 数据产生日期
	 */
	private Date createDate;

	private String ssflbm;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the invoiceCode
	 */
	public String getInvoiceCode() {
		return invoiceCode;
	}

	/**
	 * @param invoiceCode the invoiceCode to set
	 */
	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	/**
	 * @return the invoiceNo
	 */
	public String getInvoiceNo() {
		return invoiceNo;
	}

	/**
	 * @param invoiceNo the invoiceNo to set
	 */
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	/**
	 * @return the detailNo
	 */
	public Integer getDetailNo() {
		return detailNo;
	}

	/**
	 * @param detailNo the detailNo to set
	 */
	public void setDetailNo(Integer detailNo) {
		this.detailNo = detailNo;
	}

	/**
	 * @return the goodName
	 */
	public String getGoodName() {
		return goodName;
	}

	/**
	 * @param goodName the goodName to set
	 */
	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the num
	 */
	public BigDecimal getNum() {
		return num;
	}

	/**
	 * @param num the num to set
	 */
	public void setNum(BigDecimal num) {
		this.num = num;
	}

	/**
	 * @return the unitPrice
	 */
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the detailAmount
	 */
	public BigDecimal getDetailAmount() {
		return detailAmount;
	}

	/**
	 * @param detailAmount the detailAmount to set
	 */
	public void setDetailAmount(BigDecimal detailAmount) {
		this.detailAmount = detailAmount;
	}

	/**
	 * @return the taxRate
	 */
	public BigDecimal getTaxRate() {
		return taxRate;
	}

	/**
	 * @param taxRate the taxRate to set
	 */
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	/**
	 * @return the taxAmount
	 */
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	/**
	 * @param taxAmount the taxAmount to set
	 */
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getSsflbm() {
		return ssflbm;
	}

	public void setSsflbm(String ssflbm) {
		this.ssflbm = ssflbm;
	}
	
}
