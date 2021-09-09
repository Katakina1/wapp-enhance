package com.xforceplus.wapp.modules.scanRefund.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 抵账表实体(发票签收)
 */
public class EnterPackageNumberEntity implements Serializable {

    //ID
    private Long id;
    private Long userId;


    //UUID
    private String uuid;

    //发票类型
    private String invoiceType;

    //发票代码
    private String invoiceCode;

    //发票号码
    private String invoiceNo;

    //开票日期
    private String invoiceDate;

    //金额
    private String invoiceAmount;

    //税额
    private String taxAmount;

    //供应商号
    private String venderId;

    //供应商名称
    private String venderName;

    //退单号
    private String rebateNo;

    //退单时间
    private String rebateDate;

    //退货类型
    private String refundType;

    //退货编号
    private String refundNo;

    //退货备注
    private String refundRemark;

    private String[] rebateNos;

    private String[] ids;

    private String schemaLabel;

    //邮包号
    private String rebateExpressno;

    private List<InvoiceEntity> invoiceEntityList;

    //序号
    private int indexNo;

    //购方名称
    private String gfName;

    //销方名称
    private String xfName;

    //邮寄方式
    private String postType;

    private String flowType;

    private String refundReason;
    
    private String epsNo;
    
    private String refundCode;

    private String mailDate;

    private String mailCompany;

    private String qsDate;

    private String shopNo;
    private String applicantDepartment;
    private String applicantNo;
    private String applicantName;
    private String applicantCall;
    private String applicantSubarea;
    private String importDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getRebateNo() {
        return rebateNo;
    }

    public void setRebateNo(String rebateNo) {
        this.rebateNo = rebateNo;
    }

    public String getRebateDate() {
        return rebateDate;
    }

    public void setRebateDate(String rebateDate) {
        this.rebateDate = rebateDate;
    }

    public String[] getRebateNos() {
        return (rebateNos == null) ? null : Arrays.copyOf(rebateNos, rebateNos.length);
    }

    public void setRebateNos(String[] rebateNos) {
        this.rebateNos = rebateNos == null ? null : Arrays.copyOf(rebateNos, rebateNos.length);
    }

    public String[] getIds() {
        return (ids == null) ? null : Arrays.copyOf(ids, ids.length);
    }

    public void setIds(String[] ids) {
        this.ids = ids == null ? null : Arrays.copyOf(ids, ids.length);
    }


    public String getSchemaLabel() {
        return schemaLabel;
    }

    public void setSchemaLabel(String schemaLabel) {
        this.schemaLabel = schemaLabel;
    }

    public String getRebateExpressno() {
        return rebateExpressno;
    }

    public void setRebateExpressno(String rebateExpressno) {
        this.rebateExpressno = rebateExpressno;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public List<InvoiceEntity> getInvoiceEntityList() {
        return invoiceEntityList;
    }

    public void setInvoiceEntityList(List<InvoiceEntity> invoiceEntityList) {
        this.invoiceEntityList = invoiceEntityList;
    }

    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getRefundRemark() {
        return refundRemark;
    }

    public void setRefundRemark(String refundRemark) {
        this.refundRemark = refundRemark;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public int getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(int indexNo) {
        this.indexNo = indexNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

	public String getEpsNo() {
		return epsNo;
	}

	public void setEpsNo(String epsNo) {
		this.epsNo = epsNo;
	}

	public String getRefundCode() {
		return refundCode;
	}

	public void setRefundCode(String refundCode) {
		this.refundCode = refundCode;
	}
    public String getMailDate() {
        return mailDate;
    }

    public void setMailDate(String mailDate) {
        this.mailDate = mailDate;
    }

    public String getMailCompany() {
        return mailCompany;
    }

    public void setMailCompany(String mailCompany) {
        this.mailCompany = mailCompany;
    }

    public String getQsDate() {
        return qsDate;
    }

    public void setQsDate(String qsDate) {
        this.qsDate = qsDate;
    }

    public String getXfName() {
        return xfName;
    }

    public void setXfName(String xfName) {
        this.xfName = xfName;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getApplicantDepartment() {
        return applicantDepartment;
    }

    public void setApplicantDepartment(String applicantDepartment) {
        this.applicantDepartment = applicantDepartment;
    }

    public String getApplicantNo() {
        return applicantNo;
    }

    public void setApplicantNo(String applicantNo) {
        this.applicantNo = applicantNo;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantCall() {
        return applicantCall;
    }

    public void setApplicantCall(String applicantCall) {
        this.applicantCall = applicantCall;
    }

    public String getApplicantSubarea() {
        return applicantSubarea;
    }

    public void setApplicantSubarea(String applicantSubarea) {
        this.applicantSubarea = applicantSubarea;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }
}
