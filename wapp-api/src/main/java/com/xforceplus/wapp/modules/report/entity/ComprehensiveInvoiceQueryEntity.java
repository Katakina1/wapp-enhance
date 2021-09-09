package com.xforceplus.wapp.modules.report.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 抵账表实体(发票签收)
 */
@Getter
@Setter
public class ComprehensiveInvoiceQueryEntity implements Serializable {

    //ID
    private Long id;

    //发票类型
    private String invoiceType;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
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

	public String getXfTaxNo() {
		return xfTaxNo;
	}

	public void setXfTaxNo(String xfTaxNo) {
		this.xfTaxNo = xfTaxNo;
	}

	public String getXfName() {
		return xfName;
	}

	public void setXfName(String xfName) {
		this.xfName = xfName;
	}

	public Double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(Double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public String getRzhBelongDate() {
		return rzhBelongDate;
	}

	public void setRzhBelongDate(String rzhBelongDate) {
		this.rzhBelongDate = rzhBelongDate;
	}

	public String getRzhYesorno() {
		return rzhYesorno;
	}

	public void setRzhYesorno(String rzhYesorno) {
		this.rzhYesorno = rzhYesorno;
	}

	public String getQsType() {
		return qsType;
	}

	public void setQsType(String qsType) {
		this.qsType = qsType;
	}

	public String getQsStatus() {
		return qsStatus;
	}

	public void setQsStatus(String qsStatus) {
		this.qsStatus = qsStatus;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	//发票代码
    private String invoiceCode;

    //发票号码
    private String invoiceNo;

    //开票日期
    private String invoiceDate;

    //购方税号
    private String gfTaxNo;

    //购方名称
    private String gfName;

    //销方税号
    private String xfTaxNo;

    //销方名称
    private String xfName;

    //金额
    private Double invoiceAmount;

    //税额
    private Double taxAmount;

    //税价合计
    private Double totalAmount;

    //备注
    private String remark;

    //发票状态
    private String invoiceStatus;

    //状态更新时间
    private Date statusUpdateDate;

    //认证日期
    private Date rzhDate;

    //签收日期
    private Date qsDate;

    //税款所属期
    private String rzhBelongDate;

    //认证状态
    private String rzhYesorno;

    //签收类型
    private String qsType;

    //签收状态
    private String qsStatus;

    //认证结果
    private String authStatus;

    public Date getStatusUpdateDate() {
        if(statusUpdateDate == null){
            return null;
        }
        return (Date) statusUpdateDate.clone();
    }

    public void setStatusUpdateDate(Date statusUpdateDate) {
        if(statusUpdateDate == null){
            this.statusUpdateDate = null;
        }else {
            this.statusUpdateDate = (Date) statusUpdateDate.clone();
        }
    }

    public Date getRzhDate() {
        if(rzhDate == null){
            return null;
        }
        return (Date) rzhDate.clone();
    }

    public void setRzhDate(Date rzhDate) {
        if(rzhDate == null){
            this.rzhDate = null;
        }else {
            this.rzhDate = (Date) rzhDate.clone();
        }
    }

    public Date getQsDate() {
        if(qsDate == null){
            return null;
        }
        return (Date) qsDate.clone();
    }

    public void setQsDate(Date qsDate) {
        if (qsDate == null) {
            this.qsDate = null;
        }else {
            this.qsDate = (Date) qsDate.clone();
        }
    }
}
