package com.xforceplus.wapp.modules.InformationInquiry.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 扫描表
 */
public class ScanningEntity implements Serializable {

    private static final long serialVersionUID = -3948094924809212156L;
    private Long id;
    private String jvCode;//对应orgcode
    private String companyCode;//对应companyCode
    private String venderid;//供应商号
    private String gfName;//购方名称
    private String xfName;//销方名称
    private String invoiceNo;//发票号
    private String invoiceType;//发票类型
    private Date invoiceDate;//开票日期
    private BigDecimal invoiceAmount;//发票金额
    private String refundNotes;//退单原因
    private String rebateno;//退单号
    private String rebateExpressno;//邮包号
    private String rownumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
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

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getRefundNotes() {
        return refundNotes;
    }

    public void setRefundNotes(String refundNotes) {
        this.refundNotes = refundNotes;
    }

    public String getRebateno() {
        return rebateno;
    }

    public void setRebateno(String rebateno) {
        this.rebateno = rebateno;
    }

    public String getRebateExpressno() {
        return rebateExpressno;
    }

    public void setRebateExpressno(String rebateExpressno) {
        this.rebateExpressno = rebateExpressno;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }
}
