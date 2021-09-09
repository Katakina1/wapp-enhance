package com.xforceplus.wapp.modules.scanRefund.entity;


import java.io.Serializable;
import java.util.Date;

/**
 * 发票匹配
 */
public class RebatenoForQueryXiaoEntity implements Serializable {

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
    private Double invoiceAmount;

    //税额
    private Double taxAmount;

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
    private String rebateDate;

    private String flowType;

    private String usercode;
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

    public Double getInvoiceAmount() {
        if(invoiceAmount == null){
            return 0.00;
        }
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        if(invoiceAmount == null){
            this.invoiceAmount = 0.00;
        }
        this.invoiceAmount = invoiceAmount;
    }



    public Date getQsDate() {
        if(qsDate == null){
            return null;
        }
        return (Date) qsDate.clone();
    }

    public Double getTaxAmount() {
        if(taxAmount==null){
            return 0.00;
        }
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        if(taxAmount==null){
            this.taxAmount = 0.00;
        }
        this.taxAmount = taxAmount;
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

    public String getRebateDate() {
        return rebateDate;
    }

    public void setRebateDate(String rebateDate) {
        this.rebateDate = rebateDate;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
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
}
