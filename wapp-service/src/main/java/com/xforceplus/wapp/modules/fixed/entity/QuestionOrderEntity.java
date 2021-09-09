package com.xforceplus.wapp.modules.fixed.entity;

import java.math.BigDecimal;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
/**
 * 
 * @author Karry.xie
 *对应表T_dx_fixed_assets_match
 */

public class QuestionOrderEntity extends AbstractBaseDomain {
	
	private Long id;
	/*
	 * jv码1
	 */
	private String jvcode;
	private String jvname;
	/**
	 * 购方税号
	 */
	private String gfTaxNo;
	/**
	 * 购方名称1
	 */
	private String gfName;
	/**
	 * 供应商号1
	 */
	private String venderid;
	/**
	 * 供应商名称1
	 */
	private String venderName;
	/**
	 * 发票金额1
	 */
	private Double invoiceAmount; 
	/**
	 * 发票数量1
	 */
	private int invoiceCount;
	
	/*
	 * 订单金额1
	 * 
	 */
	private Double orderAmount;
	
	/**
	 * 订单数量1
	 */
	private int orderCount;
	/**
	 * 匹配时间1
	 */
	private Date matchDate;
	
	/**
	 * 结算方式1
	 */
	private String settlementMethod;
   /*
    * 匹配状态1
    */
	private String matchStatus;
	
   
    /**
     * 文件路径1
     * 对应表:t_dx_fixed_assets_file
     */
    private  String path;
    
	@Override
	public Boolean isNullObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMatchStatus() {
		return matchStatus;
	}

	public void setMatchStatus(String matchStatus) {
		this.matchStatus = matchStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJvcode() {
		return jvcode;
	}

	public void setJvcode(String jvcode) {
		this.jvcode = jvcode;
	}

	public String getJvname() {
		return jvname;
	}

	public void setJvname(String jvname) {
		this.jvname = jvname;
	}

	public String getGfTaxNo() {
		return gfTaxNo;
	}

	public void setGfTaxNo(String gfTaxNo) {
		this.gfTaxNo = gfTaxNo;
	}

	public String getGfName() {
		return gfName;
	}

	public void setGfName(String gfName) {
		this.gfName = gfName;
	}

	public String getVenderid() {
		return venderid;
	}

	public void setVenderid(String venderid) {
		this.venderid = venderid;
	}

	public String getVenderName() {
		return venderName;
	}

	public void setVenderName(String venderName) {
		this.venderName = venderName;
	}

	public Double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(Double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public int getInvoiceCount() {
		return invoiceCount;
	}

	public void setInvoiceCount(int invoiceCount) {
		this.invoiceCount = invoiceCount;
	}

	public Double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public Date getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(Date matchDate) {
		this.matchDate = matchDate;
	}

	public String getSettlementMethod() {
		return settlementMethod;
	}

	public void setSettlementMethod(String settlementMethod) {
		this.settlementMethod = settlementMethod;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
