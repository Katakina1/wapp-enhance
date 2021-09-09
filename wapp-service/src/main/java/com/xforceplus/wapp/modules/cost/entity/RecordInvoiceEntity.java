package com.xforceplus.wapp.modules.cost.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RecordInvoiceEntity extends AbstractBaseDomain {
    //发票代码
    private String invoiceCode;
    //发票号码
    private String invoiceNo;
    //来源
    private String sourceSystem;
    //发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
    private String invoiceType;
    //发票类型 1-增值税专用发票 2-增值税普通发票  3-非增值税发票
    private String invoiceKind;
    //开票日期
    private String invoiceDate;
    //发票金额
    private BigDecimal invoiceAmount;
    //税额
    private BigDecimal taxAmount;
    //价税合计
    private BigDecimal totalAmount;
    //税率
    private BigDecimal taxRate;
    //购方税号
    private String gfTaxNo;
    //购方名称
    private String gfName;
    //jvcode
    private String jvcode;
    //jvname
    private String jvname;
    //公司代码
    private String companyCode;
    //校验码
    private String checkCode;
    //大象匹配状态0,5,6未匹配,可用
    private String dxhyMatchStatus;
    //费用号,空的未使用,可用
    private String costNo;
    //是否已存在底账
    private Boolean isExist;
    //已冲销金额
    private BigDecimal coveredAmount;
    //本次冲销金额
    private BigDecimal coverAmount;
    //未冲销金额
    private BigDecimal uncoveredAmount;
    //供应商号
    private String venderid;
    //发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
    private String invoiceStatus;

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
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

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getInvoiceKind() {
        return invoiceKind;
    }

    public void setInvoiceKind(String invoiceKind) {
        this.invoiceKind = invoiceKind;
    }

    public BigDecimal getCoverAmount() {
        return coverAmount;
    }

    public void setCoverAmount(BigDecimal coverAmount) {
        this.coverAmount = coverAmount;
    }

    public BigDecimal getCoveredAmount() {
        return coveredAmount;
    }

    public void setCoveredAmount(BigDecimal coveredAmount) {
        this.coveredAmount = coveredAmount;
    }

    public BigDecimal getUncoveredAmount() {
        return uncoveredAmount;
    }

    public void setUncoveredAmount(BigDecimal uncoveredAmount) {
        this.uncoveredAmount = uncoveredAmount;
    }

    private List<RateEntity> rateTableData;

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

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
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

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
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

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getDxhyMatchStatus() {
        return dxhyMatchStatus;
    }

    public void setDxhyMatchStatus(String dxhyMatchStatus) {
        this.dxhyMatchStatus = dxhyMatchStatus;
    }

    public String getCostNo() {
        return costNo;
    }

    public void setCostNo(String costNo) {
        this.costNo = costNo;
    }

    public Boolean getIsExist() {
        return isExist;
    }

    public void setIsExist(Boolean isExist) {
        this.isExist = isExist;
    }

    public List<RateEntity> getRateTableData() {
        return rateTableData;
    }

    public void setRateTableData(List<RateEntity> rateTableData) {
        this.rateTableData = rateTableData;
    }

    @Override
    public Boolean isNullObject() {
        if(this.invoiceNo==null){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


}
