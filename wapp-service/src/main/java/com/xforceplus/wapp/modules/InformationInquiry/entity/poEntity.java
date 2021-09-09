package com.xforceplus.wapp.modules.InformationInquiry.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表
 */
public class poEntity implements Serializable {

    private static final long serialVersionUID = 7767384407466855181L;
    private Long Id;
    private String poCode;//订单号
    private String receiptId;//收货号
    private String invoiceNo;//发票号
    private String invoiceType;//发票类型
    private String receiptDate;//收货日期
    private BigDecimal receiptAmount;//收货金额
    private String hostStatus;//订单状态
    private String dxhyMatchStatus;//匹配状态
    private String venderId;//供应商号
    private String vendername;//供应商名称
    private String jvcode;//机构编码
    private String orgName;//购方名称
    private BigDecimal amountunpaid;//未付金额
    private BigDecimal amountpaid;//已付金额
    private Date dueDate;//付款期限
    private String poType;//po类型 1收货，2调整，3索赔退货，4冲账
    private String tractionNbr;//交易号
    private String rownumber;
    private BigDecimal newAmount;//最新金額
    public BigDecimal getNewAmount() {
        return newAmount;
    }

    public void setNewAmount(BigDecimal newAmount) {
        this.newAmount = newAmount;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public BigDecimal getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(BigDecimal receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public String getHostStatus() {
        return hostStatus;
    }

    public void setHostStatus(String hostStatus) {
        this.hostStatus = hostStatus;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getDxhyMatchStatus() {
        return dxhyMatchStatus;
    }

    public void setDxhyMatchStatus(String dxhyMatchStatus) {
        this.dxhyMatchStatus = dxhyMatchStatus;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public BigDecimal getAmountunpaid() {
        return amountunpaid;
    }

    public void setAmountunpaid(BigDecimal amountunpaid) {
        this.amountunpaid = amountunpaid;
    }

    public BigDecimal getAmountpaid() {
        return amountpaid;
    }

    public void setAmountpaid(BigDecimal amountpaid) {
        this.amountpaid = amountpaid;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getPoType() {
        return poType;
    }

    public void setPoType(String poType) {
        this.poType = poType;
    }

    public String getTractionNbr() {
        return tractionNbr;
    }

    public void setTractionNbr(String tractionNbr) {
        this.tractionNbr = tractionNbr;
    }

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }
}
