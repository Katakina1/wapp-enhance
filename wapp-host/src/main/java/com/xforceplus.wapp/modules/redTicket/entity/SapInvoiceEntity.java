package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import com.google.common.base.MoreObjects;

import java.math.BigDecimal;

public class SapInvoiceEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = -3322927968003764966L;
    private String uuid;//UUID
    private String documentHeaderText;//凭证抬头文本
    private String subject;//科目
    private String companyCode;//公司代码
    private String certificateNo;//凭证号
    private String documentType;//凭证类型
    private String writeOff;//冲销清账
    private String clearanceVoucher;//清账凭证
    private String clearingDate;//清账日期
    private BigDecimal showCurrencyAmount;//显示货币的金额
    private String voucherCurrency;//凭证货币
    private String reference;//参照
    private String currency;//本币
    private BigDecimal currencyAmount;//本币金额
    private String postingDate;//过账日期
    private String paymentDate;//付款日期
    private String taxCode;//税码
    private String fiscalYear;//会计年度
    private String invoiceText;//文本
    private String invoiceDate;//开票日期
    private String venderId;//6位供应商号
    private String usercode;//10位供应商号
    private String costCenter;//成本中心
    private String profitCenter;//成本中心2
    private String shopNo;

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
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

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getDocumentHeaderText() {
        return documentHeaderText;
    }

    public void setDocumentHeaderText(String documentHeaderText) {
        this.documentHeaderText = documentHeaderText;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
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

    public BigDecimal getShowCurrencyAmount() {
        return showCurrencyAmount;
    }

    public void setShowCurrencyAmount(BigDecimal showCurrencyAmount) {
        this.showCurrencyAmount = showCurrencyAmount;
    }

    public String getVoucherCurrency() {
        return voucherCurrency;
    }

    public void setVoucherCurrency(String voucherCurrency) {
        this.voucherCurrency = voucherCurrency;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public String getInvoiceText() {
        return invoiceText;
    }

    public void setInvoiceText(String invoiceText) {
        this.invoiceText = invoiceText;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("certificateNo", certificateNo+",")
                .add("invoiceDate", invoiceDate+",")
                .add("documentType", documentType+",")
                .add("postingDate", postingDate+",")
                .add("companyCode", companyCode+",")
                .add("costCenter", costCenter+",")
                .add("profitCenter", profitCenter+",")
                .add("reference", reference+",")
                .add("paymentDate", paymentDate+",")
                .add("clearanceVoucher", clearanceVoucher+",")
                .add("subject", subject+",")
                .add("usercode", usercode+",")
                .add("showCurrencyAmount", showCurrencyAmount+",")
                .add("voucherCurrency", voucherCurrency+",")
                .add("currencyAmount", currencyAmount+",")
                .add("currency", currency+",")
                .add("invoiceText", invoiceText+",")
                .add("documentHeaderText", documentHeaderText)
                .toString();
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
