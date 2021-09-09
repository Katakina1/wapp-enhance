package com.xforceplus.wapp.modules.scanRefund.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author raymond.yan
 */
public class InvoiceEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4444843280925943786L;

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
    private BigDecimal invoiceAmount;

    //税额
    private BigDecimal taxAmount;

    //税价合计
    private BigDecimal totalAmount;

    private String dxhyMatchStatus;

    //来源
    private  String systemSource;

    //是否有明细
    private  String detailYesorno;

    //供应商号
    private  String venderid;


    //退票时间
    private  String rebateDate;

    //供应商名称
    private  String vendername;

    private String jvcode;

    private Boolean isEmpty;

    private String checkNo;

    public String getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(String checkNo) {
        this.checkNo = checkNo;
    }

    public Boolean getEmpty() {
        return isEmpty;
    }

    public void setEmpty(Boolean empty) {
        isEmpty = empty;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getVendername() {
        return vendername;
    }

    public void setVendername(String vendername) {
        this.vendername = vendername;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getDxhyMatchStatus() {
        return dxhyMatchStatus;
    }

    public void setDxhyMatchStatus(String dxhyMatchStatus) {
        this.dxhyMatchStatus = dxhyMatchStatus;
    }

    public String getDetailYesorno() {
        return detailYesorno;
    }

    public void setDetailYesorno(String detailYesorno) {
        this.detailYesorno = detailYesorno;
    }

    public String getSystemSource() {
        return systemSource;
    }

    public void setSystemSource(String systemSource) {
        this.systemSource = systemSource;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }


    public String getRebateDate() {
        return rebateDate;
    }

    public void setRebateDate(String rebateDate) {
        this.rebateDate = rebateDate;
    }
}
