package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 导入页面实体
 * @author Raymond.yan
 * @date 10/20/2018
 */
public class ImportEntity extends AbstractBaseDomain {


    private static final long serialVersionUID = 3693528970374359711L;



    /**
     * 红字通知单号
     */
    @NotBlank
    @Length(max = 12)
    private String redNoticeNumber;


    /**
     * 发票代码
     */
    @NotBlank
    @Length(max = 12)
    private String invoiceCode;

    /**
     * 发票号码
     */
    @NotBlank
    @Length(max = 8)
    private String invoiceNo;

    /**
     * 开票日期
     */
    @NotBlank
    private String invoiceDate;

    /**
     * 金额
     */
    @NotBlank
    private String amount;
  /**
     * 金额
     */
    @NotBlank
    private String InvoiceAmount;




    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 发票状态
     */
    private String invoiceStatus;

    private  String totalAmount;


    private  String taxRate;

    /**
     * 税额
     */
    private String taxAmount;
/**
     * gftaxno
     */
    private String gfTaxNo;

    public String getInvoiceAmount() {
        return InvoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        InvoiceAmount = invoiceAmount;
    }

    private String xfTaxNo;
    private String venderid;
    private String jvcode;
    //序列号
    private String serialNumber;
    //公司代码
    private String companyCode;

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getXfTaxNo() {
        return xfTaxNo;
    }

    public void setXfTaxNo(String xfTaxNo) {
        this.xfTaxNo = xfTaxNo;
    }

    public String getGfTaxNo() {
        return gfTaxNo;
    }

    public void setGfTaxNo(String gfTaxNo) {
        this.gfTaxNo = gfTaxNo;
    }

    /**
     * excel导入的序号
     */
    private int indexNo;

    private Boolean isTrue;
    //校验码
    private String checkNo;

    public String getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(String checkNo) {
        this.checkNo = checkNo;
    }

    public Boolean getTrue() {
        return isTrue;
    }

    public void setTrue(Boolean aTrue) {
        isTrue = aTrue;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public int getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(int indexNo) {
        this.indexNo = indexNo;
    }

    public String getRedNoticeNumber() {
        return redNoticeNumber;
    }

    public void setRedNoticeNumber(String redNoticeNumber) {
        this.redNoticeNumber = redNoticeNumber;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
