package com.xforceplus.wapp.modules.redTicket.entity;


import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 红票匹配表
 *
 */
public class RedTicketMatch  extends AbstractBaseDomain {
    private static final long serialVersionUID = -4132928477411590834L;
    private Long id;
    private String redTicketDataSerialNumber;//生成红票资料序列号
    private Integer indexNo;//序号
    private BigDecimal redTotalAmount;//红冲总金额
    private String dataStatus;//是否上传资料 1-是 2-否
    private String noticeStatus;//是否上传红字通知单 1-是 2-否
    private String examineResult;//审核结果 1-未审核 2-同意 3-不同意
    private String examineRemarks;//审核备注
    private String redTicketStatus;//红票状态 0-正常 1-异常 2-作废'
    private String redNoticeNumber;//红字通知单号
    private String businessType;// 业务类型 1-退货 2-协议
    private Date redTicketCreationTime;//创建时间
    private String redTicketFounder;//创建人
    private Integer dataAssociation;//文件表关联字段（文件表主键，资料）
    private Integer redNoticeAssociation;//文件表关联字段（文件表主键，红通）
    private String invoiceCode;//红票代码
    private String invoiceNo;//红票号码
    private BigDecimal invoiceAmount;//红票金额
    private BigDecimal taxAmount;//红票税额
    private BigDecimal taxRate;//红票税率
    private BigDecimal totalAmount;//红票价税合计
    private String uuid;//红票uuid
    private String qsStatus;//签收状态
    private String invoiceDate;//开票日期
    private String taxRateOne;//税率
    private String venderid;//供应商号
    private String gfTaxNo;//购方税号
    private String jvcode;
    private String companyCode;//公司代码
    private String whetherOpenRedticket;//是否开红票

    public String getWhetherOpenRedticket() {
        return whetherOpenRedticket;
    }

    public void setWhetherOpenRedticket(String whetherOpenRedticket) {
        this.whetherOpenRedticket = whetherOpenRedticket;
    }

    public Date getExamineDate() {
        return examineDate;
    }

    public void setExamineDate(Date examineDate) {
        this.examineDate = examineDate;
    }

    private Date examineDate
            ;


    public Integer getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(Integer indexNo) {
        this.indexNo = indexNo;
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

    public String getTaxRateOne() {
        return taxRateOne;
    }

    public void setTaxRateOne(String taxRateOne) {
        this.taxRateOne = taxRateOne;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getQsStatus() {
        return qsStatus;
    }

    public void setQsStatus(String qsStatus) {
        this.qsStatus = qsStatus;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getRedTicketDataSerialNumber() {
        return redTicketDataSerialNumber;
    }

    public void setRedTicketDataSerialNumber(String redTicketDataSerialNumber) {
        this.redTicketDataSerialNumber = redTicketDataSerialNumber;
    }

    public BigDecimal getRedTotalAmount() {
        return redTotalAmount;
    }

    public void setRedTotalAmount(BigDecimal redTotalAmount) {
        this.redTotalAmount = redTotalAmount;
    }

    public String getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getNoticeStatus() {
        return noticeStatus;
    }

    public void setNoticeStatus(String noticeStatus) {
        this.noticeStatus = noticeStatus;
    }

    public String getExamineResult() {
        return examineResult;
    }

    public void setExamineResult(String examineResult) {
        this.examineResult = examineResult;
    }

    public String getExamineRemarks() {
        return examineRemarks;
    }

    public void setExamineRemarks(String examineRemarks) {
        this.examineRemarks = examineRemarks;
    }

    public String getRedTicketStatus() {
        return redTicketStatus;
    }

    public void setRedTicketStatus(String redTicketStatus) {
        this.redTicketStatus = redTicketStatus;
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

    public Date getRedTicketCreationTime() {
        return redTicketCreationTime;
    }

    public void setRedTicketCreationTime(Date redTicketCreationTime) {
        this.redTicketCreationTime = redTicketCreationTime;
    }

    public String getRedTicketFounder() {
        return redTicketFounder;
    }

    public void setRedTicketFounder(String redTicketFounder) {
        this.redTicketFounder = redTicketFounder;
    }

    public Integer getDataAssociation() {
        return dataAssociation;
    }

    public void setDataAssociation(Integer dataAssociation) {
        this.dataAssociation = dataAssociation;
    }

    public Integer getRedNoticeAssociation() {
        return redNoticeAssociation;
    }

    public void setRedNoticeAssociation(Integer redNoticeAssociation) {
        this.redNoticeAssociation = redNoticeAssociation;
    }

    public String getGfTaxNo() {
        return gfTaxNo;
    }

    public void setGfTaxNo(String gfTaxNo) {
        this.gfTaxNo = gfTaxNo;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
