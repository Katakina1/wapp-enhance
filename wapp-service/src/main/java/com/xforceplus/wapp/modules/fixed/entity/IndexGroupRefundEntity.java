package com.xforceplus.wapp.modules.fixed.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 抵账表实体(发票签收)
 */
public class IndexGroupRefundEntity implements Serializable {

    //ID
    private Long id;

    //UUID
    private String uuid;

    //UUID
    private String[] uuids;

    //购方名称
    private String gfName;

    //金额
    private BigDecimal invoiceAmount;

    //供应商号
    private String venderId;

    //供应商编码
    private String venderTaxNo;

    //供应商名称
    private String venderName;

    //PO金额
    private BigDecimal amountUnpaid;


    //索赔金额
    private BigDecimal claimAmount1;

    private String createDate;

    private String invoiceDate;

    //税率
    private BigDecimal taxRate;

    //发票类型
    private String invoiceType;

    //发票代码
    private String invoiceCode;

    //发票号码
    private String invoiceNo;

    //购方税号
    private String gfTaxNo;

    //销方税号
    private String xfTaxNo;

    //销方名称
    private String xfName;

    //税额
    private BigDecimal taxAmount;

    //税价合计
    private BigDecimal totalAmount;

    //备注
    private String remark;

    //发票状态
    private String invoiceStatus;

    //扫描流水号
    private String invoiceSerialNo;

    //订单号
    private String poCode;

    //订单金额
    private BigDecimal orderAmount;
    //订单数量
    private Long orderCount;

    //收货号
    private String receiptId;

    //收货金额
    private BigDecimal receiptAmount1;

    //收货日期
    private  String receiptDate;

    //索赔号
    private String claimNo;

    //索赔日期
    private  String postDate;

    //装订册号
    private  String bbindingNo;

    private String schemaLabel;

    //是否整组退
    private String refundYesorno;

    //退票理由
    private String refundNotes;

    //发票总金额
    private BigDecimal settlementAmount;

    //发票数量
    private Long invoiceNum;
    private Long invoiceCount;

    //po总金额
    private BigDecimal poAmonut;

    //po数量
    private Long poNum;

    //索赔总金额
    private BigDecimal claimAmount;

    //索赔数量
    private Long claimNum;

    //匹配日期
    private String matchDate;

    //退单号
    private  String rebateNo;

    private Long[] ids;

    private  String refundReason;

    private  String reasonForCancel;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String[] getUuids() {
        return (uuids == null) ? null : Arrays.copyOf(uuids, uuids.length);
    }

    public void setUuids(String[] uuids) {
        this.uuids = uuids == null ? null : Arrays.copyOf(uuids, uuids.length);
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getVenderTaxNo() {
        return venderTaxNo;
    }

    public void setVenderTaxNo(String venderTaxNo) {
        this.venderTaxNo = venderTaxNo;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public BigDecimal getAmountUnpaid() {
        return amountUnpaid;
    }

    public void setAmountUnpaid(BigDecimal amountUnpaid) {
        this.amountUnpaid = amountUnpaid;
    }

    public BigDecimal getClaimAmount1() {
        return claimAmount1;
    }

    public void setClaimAmount1(BigDecimal claimAmount1) {
        this.claimAmount1 = claimAmount1;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
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

    public String getGfTaxNo() {
        return gfTaxNo;
    }

    public void setGfTaxNo(String gfTaxNo) {
        this.gfTaxNo = gfTaxNo;
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

    public String getInvoiceSerialNo() {
        return invoiceSerialNo;
    }

    public void setInvoiceSerialNo(String invoiceSerialNo) {
        this.invoiceSerialNo = invoiceSerialNo;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public BigDecimal getReceiptAmount1() {
        return receiptAmount1;
    }

    public void setReceiptAmount1(BigDecimal receiptAmount1) {
        this.receiptAmount1 = receiptAmount1;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }

    public String getBbindingNo() {
        return bbindingNo;
    }

    public void setBbindingNo(String bbindingNo) {
        this.bbindingNo = bbindingNo;
    }

    public String getSchemaLabel() {
        return schemaLabel;
    }

    public void setSchemaLabel(String schemaLabel) {
        this.schemaLabel = schemaLabel;
    }

    public String getRefundYesorno() {
        return refundYesorno;
    }

    public void setRefundYesorno(String refundYesorno) {
        this.refundYesorno = refundYesorno;
    }

    public String getRefundNotes() {
        return refundNotes;
    }

    public void setRefundNotes(String refundNotes) {
        this.refundNotes = refundNotes;
    }

    public Long getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(Long invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public BigDecimal getPoAmonut() {
        return poAmonut;
    }

    public void setPoAmonut(BigDecimal poAmonut) {
        this.poAmonut = poAmonut;
    }

    public Long getPoNum() {
        return poNum;
    }

    public void setPoNum(Long poNum) {
        this.poNum = poNum;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public Long getClaimNum() {
        return claimNum;
    }

    public void setClaimNum(Long claimNum) {
        this.claimNum = claimNum;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getRebateNo() {
        return rebateNo;
    }

    public void setRebateNo(String rebateNo) {
        this.rebateNo = rebateNo;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(Long invoiceCount) {
        this.invoiceCount = invoiceCount;
    }

    public Long[] getIds() {
        return (ids == null) ? null : Arrays.copyOf(ids, ids.length);
    }

    public void setIds(Long[] ids) {
        this.ids = ids == null ? null : Arrays.copyOf(ids, ids.length);
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getReasonForCancel() {
        return reasonForCancel;
    }

    public void setReasonForCancel(String reasonForCancel) {
        this.reasonForCancel = reasonForCancel;
    }
}
