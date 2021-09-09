package com.xforceplus.wapp.modules.report.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 匹配表
 */
public class MatchEntity implements Serializable {

    private static final long serialVersionUID = -4933985215676749999L;
    private Long id;
    private String gfName;//购方名称
    private String vendername;//供应商名称
    private String venderId;//供应商编码
    private String invoiceNo;//发票号码
    private String invoiceType;//发票类型
    private BigDecimal invoiceAmount;//发票金额
    private Integer invoiceNum;//发票数量
    private BigDecimal poAmount;//PO金额
    private Integer poNum;//PO数量
    private BigDecimal claimAmount;//索赔金额
    private Integer claimNum;//索赔单数量
    private Date matchDate;//匹配日期
    private BigDecimal settlementAmount;//结算金额
    private String orgcode;//orgcode
    private String companyCode;//company_code
    private String hostStatus;//host_status

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getVendername() {
        return vendername;
    }

    public void setVendername(String vendername) {
        this.vendername = vendername;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public Integer getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(Integer invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public BigDecimal getPoAmount() {
        return poAmount;
    }

    public void setPoAmount(BigDecimal poAmount) {
        this.poAmount = poAmount;
    }

    public Integer getPoNum() {
        return poNum;
    }

    public void setPoNum(Integer poNum) {
        this.poNum = poNum;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public Integer getClaimNum() {
        return claimNum;
    }

    public void setClaimNum(Integer claimNum) {
        this.claimNum = claimNum;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getHostStatus() {
        return hostStatus;
    }

    public void setHostStatus(String hostStatus) {
        this.hostStatus = hostStatus;
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
}
