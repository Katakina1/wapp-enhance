/**
 * 
 */
package com.xforceplus.wapp.modules.job.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;


/** 
 * <p>Title:  RecordInvoiceDetail</p>
 * <p>Description: 货运票电子底账明细</p> 
 * @author zudh
 * @date 2016年12月21日 上午10:26:24 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportInvoiceDetail  {

	private static final long serialVersionUID = -422068344392372903L;
	
	/**
	 * 主键
	 */
	@JsonIgnore
	private Long id;
	/**
	 * 明细序号
	 */
	private Integer detailNo;
	
	/**
	 * 费用项目
	 */
	private String costItem ;
	
	/**
	 * 费用金额
	 */
	private BigDecimal costAmount ;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDetailNo() {
		return detailNo;
	}

	public void setDetailNo(Integer detailNo) {
		this.detailNo = detailNo;
	}

	public String getCostItem() {
		return costItem;
	}

	public void setCostItem(String costItem) {
		this.costItem = costItem;
	}

	public BigDecimal getCostAmount() {
		return costAmount;
	}

	public void setCostAmount(BigDecimal costAmount) {
		this.costAmount = costAmount;
	}

	
	
}
