package com.xforceplus.wapp.modules.InformationInquiry.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DataInvoiceQueryEntity implements Serializable {
    //ID
    private Long id;

    //税率
    private BigDecimal taxRate;

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

    //凭证号
    private  String certificateNo;

    //匹配状态
    private  String dxhyMatchStatus;

    //匹配日期
    private Date matchDate;

    //host状态
    private  String hoststatus;
    //扫描流水号
    private  String scanningSeriano;
    //装订册号
    private  String bbindingno;
    //装箱号
    private  String packingno;
    //供应商号
    private String venderId;
    //供应商名称
    private String venderName;
    //扫描匹配日期
    private Date scanMatchDate;
    //数据发票提交统计用，非数据库字段
    private int countNum;

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
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

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getDxhyMatchStatus() {
        return dxhyMatchStatus;
    }

    public void setDxhyMatchStatus(String dxhyMatchStatus) {
        this.dxhyMatchStatus = dxhyMatchStatus;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public String getHoststatus() {
        return hoststatus;
    }

    public void setHoststatus(String hoststatus) {
        this.hoststatus = hoststatus;
    }

    public String getScanningSeriano() {
        return scanningSeriano;
    }

    public void setScanningSeriano(String scanningSeriano) {
        this.scanningSeriano = scanningSeriano;
    }

    public String getBbindingno() {
        return bbindingno;
    }

    public void setBbindingno(String bbindingno) {
        this.bbindingno = bbindingno;
    }

    public String getPackingno() {
        return packingno;
    }

    public void setPackingno(String packingno) {
        this.packingno = packingno;
    }


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

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public Date getScanMatchDate() {
        return scanMatchDate;
    }

    public void setScanMatchDate(Date date) {
        this.scanMatchDate = date;
    }

    public int getCountNum() {
        return countNum;
    }


}
