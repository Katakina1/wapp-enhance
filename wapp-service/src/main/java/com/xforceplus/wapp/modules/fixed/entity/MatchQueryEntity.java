package com.xforceplus.wapp.modules.fixed.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 匹配查询
 * */
public class MatchQueryEntity extends BaseEntity implements Serializable {
    //主键
    private Long id;
    //jvcode
    private String jvcode;
    //jvname
    private String jvname;
    //购方名称
    private String gfTaxNo;
    //购方名称
    private String gfName;
    //供应商名称
    private String venderName;
    //供应商编码
    private String venderid;
    //发票金额
    private BigDecimal invoiceAmount;
    //发票数量
    private Integer invoiceCount;
    //订单金额
    private BigDecimal orderAmount;
    //订单数量
    private Integer orderCount;
    //匹配日期
    private String matchDate;
    //结算方式
    private String settlementMethod;
    //匹配状态
    private String matchStatus;
    //扫描匹配状态
    private String scanMatchStatus;
    //审核结果
    private String checkStatus;
    //审核时间
    private String checkDate;
    //匹配的订单信息
    private List<OrderEntity> orderList;
    //匹配的发票信息
    private List<InvoiceEntity> invoiceList;
    //问题单文件列表
    private List<FileEntity> fileList;

    //审核失败原因
    private String checkFailReason;

    //销方名称
    private String xfTaxNo;
    //销方名称
    private String xfName;

    public String getScanMatchStatus() {
        return scanMatchStatus;
    }

    public void setScanMatchStatus(String scanMatchStatus) {
        this.scanMatchStatus = scanMatchStatus;
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

    public String getCheckFailReason() {
        return checkFailReason;
    }

    public void setCheckFailReason(String checkFailReason) {
        this.checkFailReason = checkFailReason;
    }

    public List<FileEntity> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileEntity> fileList) {
        this.fileList = fileList;
    }

    public List<OrderEntity> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderEntity> orderList) {
        this.orderList = orderList;
    }

    public List<InvoiceEntity> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(List<InvoiceEntity> invoiceList) {
        this.invoiceList = invoiceList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public Integer getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(Integer invoiceCount) {
        this.invoiceCount = invoiceCount;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getSettlementMethod() {
        return settlementMethod;
    }

    public void setSettlementMethod(String settlementMethod) {
        this.settlementMethod = settlementMethod;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }
}
