package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author raymond.yan
 */
public class InvoiceEntity extends AbstractBaseDomain implements Serializable {

    private static final long serialVersionUID = -166140268954468243L;
    //税率
    private String  taxRate;

    //UUID
    private String uuid;

    //发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
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

    //付款金额
    private BigDecimal paymentAmount;

    //可红冲金额
    private BigDecimal redMoneyAmount;

    //来源
    private  String systemSource;

    //是否有明细
    private  String detailYesorno;

    //发票状态  0-正常  1-失控 2-作废  3-红冲 4-异常
    private String invoiceStatus;

    //发票状态修改时间
    private Date statusUpdateDate;

    //是否认证 0-未认证 1-已认证
    private String rzhYesorno;

    //是否有效 1-有效 0-无效
    private String valid;

    //结算金额
    private BigDecimal settlementAmount;

    //供应商号
    private String venderid;

    //匹配额关联号
    private String matchno;

    private String redTicketDataSerialNumber;//生成红票资料序列号
    private String redNoticeNumber;//红字通知单号
    private String businessType;// 业务类型 1-退货 2-协议 3-折让
    private BigDecimal redTotalAmount;//红冲总金额
    private String qsStatus;//签收状态
    private BigDecimal redRushAmount;//红冲总金额
    private String jvcode;//jvcode
    private String companyCode;//公司代码
    //供应商名称
    private  String vendername;
    //供应商税号
    private  String vendertaxno;

    public String getVendertaxno() {
        return vendertaxno;
    }

    public void setVendertaxno(String vendertaxno) {
        this.vendertaxno = vendertaxno;
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

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public BigDecimal getRedRushAmount() {
        return redRushAmount;
    }

    public void setRedRushAmount(BigDecimal redRushAmount) {
        this.redRushAmount = redRushAmount;
    }

    private String pdfDateStart;//pdf导出开始时间
    private String pdfDateEnd;//pdf导出结束时间


    public String getPdfDateStart() {
        return pdfDateStart;
    }

    public void setPdfDateStart(String pdfDateStart) {
        this.pdfDateStart = pdfDateStart;
    }

    public String getPdfDateEnd() {
        return pdfDateEnd;
    }

    public void setPdfDateEnd(String pdfDateEnd) {
        this.pdfDateEnd = pdfDateEnd;
    }

    public String getQsStatus() {
        return qsStatus;
    }

    public void setQsStatus(String qsStatus) {
        this.qsStatus = qsStatus;
    }

    public String getRedTicketDataSerialNumber() {
        return redTicketDataSerialNumber;
    }

    public void setRedTicketDataSerialNumber(String redTicketDataSerialNumber) {
        this.redTicketDataSerialNumber = redTicketDataSerialNumber;
    }

    public String getRedNoticeNumber() {
        return redNoticeNumber;
    }

    public void setRedNoticeNumber(String redNoticeNumber) {
        this.redNoticeNumber = redNoticeNumber;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getDetailYesorno() {
        return detailYesorno;
    }

    public void setDetailYesorno(String detailYesorno) {
        this.detailYesorno = detailYesorno;
    }

    public String getSystemSource() { return systemSource; }

    public void setSystemSource(String systemSource) { this.systemSource = systemSource; }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
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

    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }

    public void setRedMoneyAmount(BigDecimal redMoneyAmount) { this.redMoneyAmount = redMoneyAmount; }

    public BigDecimal getPaymentAmount() { return paymentAmount; }

    public BigDecimal getRedMoneyAmount() { return redMoneyAmount; }

    public void setInvoiceStatus(String invoiceStatus) { this.invoiceStatus = invoiceStatus; }

    public String getInvoiceStatus() { return invoiceStatus; }

    public void setStatusUpdateDate(Date statusUpdateDate) { this.statusUpdateDate = statusUpdateDate; }

    public void setRzhYesorno(String rzhYesorno) { this.rzhYesorno = rzhYesorno; }

    public void setValid(String valid) { this.valid = valid; }

    public void setVenderid(String venderid) { this.venderid = venderid; }

    public Date getStatusUpdateDate() { return statusUpdateDate; }

    public String getRzhYesorno() { return rzhYesorno; }

    public String getValid() { return valid; }

    public String getVenderid() { return venderid; }

    public BigDecimal getRedTotalAmount() { return redTotalAmount; }

    public void setRedTotalAmount(BigDecimal redTotalAmount) { this.redTotalAmount = redTotalAmount; }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public String getMatchno() {
        return matchno;
    }

    public void setMatchno(String matchno) {
        this.matchno = matchno;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
