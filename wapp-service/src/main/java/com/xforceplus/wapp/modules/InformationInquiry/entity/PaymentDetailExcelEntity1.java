package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.xforceplus.wapp.modules.base.entity.UserEntity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * t_dx_dsign
 * @author
 */
public class PaymentDetailExcelEntity1 extends BaseRowModel implements Serializable {

    /**
     * 凭证抬头文本
     */
    private String documentHeaderText;

    /**
     * 文本
     */
    @ExcelProperty(value={"文本摘要"},index = 18)
    private String invoiceText;

    /**
     * 科目
     */
    @ExcelProperty(value={"科目"},index = 12)
    private String subject;

    /**
     * 公司代码
     */
    @ExcelProperty(value={"公司代码"},index = 6)
    private String companyCode;

    /**
     * 凭证类型
     */
    @ExcelProperty(value={"凭证类型"},index = 4)
    private String documentType;

    /**
     * 凭证编号
     */
    @ExcelProperty(value={"凭证号"},index = 2)
    private String certificateNo;

    /**
     * 冲销清账
     */
    private String writeOff;

    /**
     * 清账凭证
     */
    @ExcelProperty(value={"清账凭证号"},index = 11)
    private String clearanceVoucher;

    /**
     * 清账日期
     */
    private String clearingDate;

    /**
     * 参照(发票号码)
     */
    @ExcelProperty(value={"参照"},index = 9)
    private String referTo;

    /**
     * 显示的货币的金额
     */
    @ExcelProperty(value={"金额"},index = 14)
    private String showCurrencyAmount;

    /**
     * 凭证货币
     */
    @ExcelProperty(value={"币种"},index = 15)
    private String voucherCurrency;

    /**
     * 本币（发票金额）
     */
    @ExcelProperty(value={"本地币种"},index = 17)
    private String currency;

    /**
     * 本币金额
     */
    @ExcelProperty(value={"金额2"},index = 16)
    private BigDecimal currencyAmount;

    /**
     * 过账时间
     */
    @ExcelProperty(value={"过账日期"},index = 5)
    private String postingDate;

    /**
     * 付款时间
     */
    @ExcelProperty(value={"清账日期(付款日期)"},index = 10)
    private String paymentDate;

    /**
     * 税码
     */
    private String taxCode;

    /**
     * 会计年度
     */
    private String fiscalYear;

    /**
     * jv
     */
    private String jvcode;

    /**
     * 供应商号
     */

    @ExcelProperty(value={"供应商号"},index = 0)
    private String venderid;//供应商号

    /**
     * 供应商名称(非表字段)
     */
    @ExcelProperty(value={"供应商名称"},index = 1)
    private String orgName;

    /**
     * 关联用户表
     */
    private UserEntity userEntity;

    /**
     * 创建时间
     */
    private String creationDate;

    /**
     * 修改时间
     */
    private String amendDate;

    //成本中心
    @ExcelProperty(value={"成本中心"},index = 7)
    private String costCenter;

    //成本中心2
    @ExcelProperty(value={"成本中心2"},index = 8)
    private String profitCenter;
    @ExcelProperty(value={"开票日期"},index = 3)
    private String invoiceDate;

    //10位供应商号
    @ExcelProperty(value={"10位供应商号"},index = 13)
    private String usercode;

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getProfitCenter() {
        return profitCenter;
    }

    public void setProfitCenter(String profitCenter) {
        this.profitCenter = profitCenter;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getDocumentHeaderText() {
        return documentHeaderText;
    }

    public void setDocumentHeaderText(String documentHeaderText) {
        this.documentHeaderText = documentHeaderText;
    }

    public String getInvoiceText() {
        return invoiceText;
    }

    public void setInvoiceText(String invoiceText) {
        this.invoiceText = invoiceText;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getWriteOff() {
        return writeOff;
    }

    public void setWriteOff(String writeOff) {
        this.writeOff = writeOff;
    }

    public String getClearanceVoucher() {
        return clearanceVoucher;
    }

    public void setClearanceVoucher(String clearanceVoucher) {
        this.clearanceVoucher = clearanceVoucher;
    }

    public String getClearingDate() {
        return clearingDate;
    }

    public void setClearingDate(String clearingDate) {
        this.clearingDate = clearingDate;
    }

    public String getReferTo() {
        return referTo;
    }

    public void setReferTo(String referTo) {
        this.referTo = referTo;
    }

    public String getShowCurrencyAmount() {
        return showCurrencyAmount;
    }

    public void setShowCurrencyAmount(String showCurrencyAmount) {
        this.showCurrencyAmount = showCurrencyAmount;
    }

    public String getVoucherCurrency() {
        return voucherCurrency;
    }

    public void setVoucherCurrency(String voucherCurrency) {
        this.voucherCurrency = voucherCurrency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(BigDecimal currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAmendDate() {
        return amendDate;
    }

    public void setAmendDate(String amendDate) {
        this.amendDate = amendDate;
    }
}