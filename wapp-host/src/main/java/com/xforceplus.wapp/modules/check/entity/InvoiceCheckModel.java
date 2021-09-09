package com.xforceplus.wapp.modules.check.entity;

import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Bobby
 * @date 2018/4/19
 * 查验
 */
@Setter
@Getter
@ToString
public final class InvoiceCheckModel {


    private Integer id;

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(String handleDate) {
		this.handleDate = handleDate;
	}

	public String getHandleCode() {
		return handleCode;
	}

	public void setHandleCode(String handleCode) {
		this.handleCode = handleCode;
	}

	public String getHandleCourse() {
		return handleCourse;
	}

	public void setHandleCourse(String handleCourse) {
		this.handleCourse = handleCourse;
	}

	public String getCheckMassege() {
		return checkMassege;
	}

	public void setCheckMassege(String checkMassege) {
		this.checkMassege = checkMassege;
	}

	public String getCheckUser() {
		return checkUser;
	}

	public void setCheckUser(String checkUser) {
		this.checkUser = checkUser;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
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

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public Integer getDetailId() {
		return detailId;
	}

	public void setDetailId(Integer detailId) {
		this.detailId = detailId;
	}

	public String getStringTotalAmount() {
		return stringTotalAmount;
	}

	public void setStringTotalAmount(String stringTotalAmount) {
		this.stringTotalAmount = stringTotalAmount;
	}

	public ResponseInvoice getResponseInvoice() {
		return responseInvoice;
	}

	public void setResponseInvoice(ResponseInvoice responseInvoice) {
		this.responseInvoice = responseInvoice;
	}

	/**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 处理时间
     */
    private String handleDate;

    /**
     * 处理结果
     */
    private String handleCode;

    /**
     * 处理过程
     */
    private String handleCourse;

    /**
     * 描述
     */
    private String checkMassege;

    /**
     * 查验账号
     */
    private String checkUser;

    /**
     * 开票日期
     */
    private String invoiceDate;

    /**
     * 购方名称
     */
    private String buyerName;

    /**
     * 价税合计
     */
    private Double totalAmount;

    /**
     * 发票金额
     */
    private Double invoiceAmount;

    /**
     * 税额
     */
    private Double taxAmount;

    /**
     * 类型
     */
    private String invoiceType;

    /**
     * 校验码
     */
    private String checkCode;


    /**
     * detailId
     */
    private Integer detailId;

    /**
     * 价税金额大写
     */
    private String stringTotalAmount;

    /**
     * 查验响应实体
     */
    private ResponseInvoice responseInvoice;
}
