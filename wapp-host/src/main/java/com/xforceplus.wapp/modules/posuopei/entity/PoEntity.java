package com.xforceplus.wapp.modules.posuopei.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author raymond.yan
 */
public class PoEntity extends BaseEntity implements Serializable {


    private static final long serialVersionUID = 5390548904210045761L;
    public PoEntity(){

    }

    public PoEntity(String pocode,String venderid,String receiptid,BigDecimal receiptAmount,String poType,Date receiptdate,BigDecimal taxrate,String hoststatus,String invoiceId,String tractionNbr,Date tractionDate,String jvcode,String vendername,String tractionId,Date dueDate,String dept,String seq,String tractionIdSeq){
        this.pocode=pocode;
        this.venderid=venderid;
        this.receiptid=receiptid;
        this.receiptAmount=receiptAmount;
        this.poType=poType;
        this.receiptdate=receiptdate;
        this.taxrate=taxrate;
        this.hoststatus=hoststatus;
        this.invoiceId=invoiceId;
        this.tractionNbr=tractionNbr;
        this.dept=dept;


        this.tractionDate=tractionDate;
        this.vendername=vendername;
        this.jvcode=jvcode;
        this.tractionId=tractionId;
        this.dueDate=dueDate;
        this.seq=seq;
        this.tractionIdSeq=tractionIdSeq;
    }

    private Integer id;
    /**
     * PO号
     */
    private String pocode;

    /**
     * 供应商号
     */
    private String venderid;

    /**
     * 付款期限
     */
    private Date dueDate;
    /**
     * 收货号
     */
    private String receiptid;

    //变更金额
    private BigDecimal changeAmount;
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 收货金额（不含税金额）
     */
    private BigDecimal receiptAmount;

    /**
     * po类型
     */
    private String poType;

    /**
     * 收货日期
     */
    private Date receiptdate;

    /**
     * 税率
     */
    private BigDecimal taxrate;

    /**
     * 购方名称
     */
    private String gfname;

    /**
     * 已付金额
     */
    private BigDecimal amountpaid;

    /**
     * 未付金额
     */
    private BigDecimal amountunpaid;

    /**
     * 机构代码
     */
    private  String jvcode;

    /**
     *供应商名称
     */
    private  String vendername;

    /**
     *host结算状态（0-未处理，1-host处理，2-sap处理，3-扫描）
     */
    private  String hoststatus;

    /**
     *错误号
     */
    private  String errcode;

    /**
     *错误描述
     */
    private  String errdesc;

    /**
     *大象平台匹配状态（PO、索赔与发票匹配关系号）
     */
    private  String dxhyMatchStatus;

    /**
     *打印号
     */
    private  String printcode;

    /**
     *匹配关联号
     */
    private  String matchno;


      /**
     *发票处理状态（0-未处理，1-host处理，2-sap处理，3-扫描）
     */
    private  String invoiceDealStatus;

    /**
     *发票号码
     */
    private  String invoiceno;

    /**
     *凭证号
     */
    private  String certificateno;

    /**
     * fapiaoid
     */
    private String invoiceId;

    /**
     * jiao yi ri qi
     */
    private Date tractionDate;

    /**
     * 成本中心
     */
    private  String divstore;

    /**
     * 账号
     */
    private  String acc;

    private String tractionNbr;

    private Boolean isEmpty;

    private String tractionId;

    private String dept;

    private  String seq;

    private  String tractionIdSeq;

    private String storeNbr;
    private Date postDate;

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getStoreNbr() {
        return storeNbr;
    }

    public void setStoreNbr(String storeNbr) {
        this.storeNbr = storeNbr;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getTractionIdSeq() {
        return tractionIdSeq;
    }

    public void setTractionIdSeq(String tractionIdSeq) {
        this.tractionIdSeq = tractionIdSeq;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getTractionId() {
        return tractionId;
    }

    public void setTractionId(String tractionId) {
        this.tractionId = tractionId;
    }

    public String getDivstore() {
        return divstore;
    }

    public void setDivstore(String divstore) {
        this.divstore = divstore;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Date getTractionDate() {
        return tractionDate;
    }

    public void setTractionDate(Date tractionDate) {
        this.tractionDate = tractionDate;
    }

    public String getTractionNbr() {
        return tractionNbr;
    }

    public void setTractionNbr(String tractionNbr) {
        this.tractionNbr = tractionNbr;
    }

    public String getPoType() {
        return poType;
    }

    public void setPoType(String poType) {
        this.poType = poType;
    }

    public Boolean getEmpty() {
        return isEmpty;
    }

    public void setEmpty(Boolean empty) {
        isEmpty = empty;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPocode() {
        return pocode;
    }

    public void setPocode(String pocode) {
        this.pocode = pocode;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getReceiptid() {
        return receiptid;
    }

    public void setReceiptid(String receiptid) {
        this.receiptid = receiptid;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(BigDecimal receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public Date getReceiptdate() {
        return receiptdate;
    }

    public void setReceiptdate(Date receiptdate) {
        this.receiptdate = receiptdate;
    }

    public BigDecimal getTaxrate() {
        return taxrate;
    }

    public void setTaxrate(BigDecimal taxrate) {
        this.taxrate = taxrate;
    }

    public String getGfname() {
        return gfname;
    }

    public void setGfname(String gfname) {
        this.gfname = gfname;
    }

    public BigDecimal getAmountpaid() {
        return amountpaid;
    }

    public void setAmountpaid(BigDecimal amountpaid) {
        this.amountpaid = amountpaid;
    }

    public BigDecimal getAmountunpaid() {
        return amountunpaid;
    }

    public void setAmountunpaid(BigDecimal amountunpaid) {
        this.amountunpaid = amountunpaid;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getVendername() {
        return vendername;
    }

    public void setVendername(String vendername) {
        this.vendername = vendername;
    }

    public String getHoststatus() {
        return hoststatus;
    }

    public void setHoststatus(String hoststatus) {
        this.hoststatus = hoststatus;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrdesc() {
        return errdesc;
    }

    public void setErrdesc(String errdesc) {
        this.errdesc = errdesc;
    }

    public String getDxhyMatchStatus() {
        return dxhyMatchStatus;
    }

    public void setDxhyMatchStatus(String dxhyMatchStatus) {
        this.dxhyMatchStatus = dxhyMatchStatus;
    }

    public String getPrintcode() {
        return printcode;
    }

    public void setPrintcode(String printcode) {
        this.printcode = printcode;
    }

    public String getMatchno() {
        return matchno;
    }

    public void setMatchno(String matchno) {
        this.matchno = matchno;
    }

    public String getInvoiceDealStatus() {
        return invoiceDealStatus;
    }

    public void setInvoiceDealStatus(String invoiceDealStatus) {
        this.invoiceDealStatus = invoiceDealStatus;
    }

    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    public String getCertificateno() {
        return certificateno;
    }

    public void setCertificateno(String certificateno) {
        this.certificateno = certificateno;
    }
}
