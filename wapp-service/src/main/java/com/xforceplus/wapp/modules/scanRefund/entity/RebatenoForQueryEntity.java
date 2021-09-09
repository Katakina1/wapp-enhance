package com.xforceplus.wapp.modules.scanRefund.entity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票匹配
 */
public class RebatenoForQueryEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    //发票代码
    private String invoiceCode;
    //发票号码
    private String invoiceNo;
    //开票日期
    private Date createDate;

    //签收日期
    private Date qsDate;

    //签收状态
    private String qsStatus;


    //购方名称
    private String gfName;

    //销方名称
    private String xfName;

    //金额
    private BigDecimal invoiceAmount;

    //税额
    private BigDecimal taxAmount;

    private String jvCode;
    private String companyCode;

    private String invoiceType;
    private  String venderid;
    private String notes;

    //退单号
    private  String rebateNo;

    //邮包号
    private String rebateExpressno;

    //退单时间
    private Date rebateDate;

    private String flowType;

    private String epsNo;

    private String mailCompany;
    private String mailDate;
    private String rownumber;

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getQsStatus() {
        return qsStatus;
    }

    public void setQsStatus(String qsStatus) {
        this.qsStatus = qsStatus;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getXfName() {
        return xfName;
    }

    public void setXfName(String xfName) {
        this.xfName = xfName;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getJvCode() {
        return jvCode;
    }

    public void setJvCode(String jvCode) {
        this.jvCode = jvCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRebateNo() {
        return rebateNo;
    }

    public void setRebateNo(String rebateNo) {
        this.rebateNo = rebateNo;
    }

    public String getRebateExpressno() {
        return rebateExpressno;
    }

    public void setRebateExpressno(String rebateExpressno) {
        this.rebateExpressno = rebateExpressno;
    }

    public Date getRebateDate() {
        return rebateDate;
    }

    public void setRebateDate(Date rebateDate) {
        this.rebateDate = rebateDate;
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

    public String getMailCompany() {
        return mailCompany;
    }

    public void setMailCompany(String mailCompany) {
        this.mailCompany = mailCompany;
    }

    public String getMailDate() {
        return mailDate;
    }

    public void setMailDate(String mailDate) {
        this.mailDate = mailDate;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
}
