package com.xforceplus.wapp.modules.posuopei.entity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author raymond.yan
 */
public class MatchEntity implements Serializable {
    private static final long serialVersionUID = 9123425083707071044L;


    private Integer id;
    //错误提示
    private String mathingSource;
    //匹配状态
    private String matchingType;
    //发票金额合计(结算金额)
    private BigDecimal invoiceAmount;
    //po 金额合计
    private BigDecimal poAmount;
    //claim金额合计
    private BigDecimal claimAmount;

    //取消匹配原因
    private String reasonForCancel;

    //match_remarks
    private String matchRemarks;
    //
    private  String hoststatus;
    private String gfName;
    private  String printcode;

    //
    private Date matchDate;
    //

    private int poNum;

    private BigDecimal settlementamount;

    private BigDecimal cover;
    //购方税号
    private  String gfTaxNo;

    private int claimNum;
    //发票数量
    private int invoiceNum;
    private String venderid;
    //票单关联号
    private String matchno;
    private List<PoEntity> poEntityList;
    private List<String> imgList;

    private Boolean isEmpty;

    //扫描匹配状态
    private  String scanMactchStatus;

    //供应商名称
    private  String venderName;

    //匹配差额
    private BigDecimal matchCover;
    //jv
    private String jvcode;

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public BigDecimal getMatchCover() {
        return matchCover;
    }

    public void setMatchCover(BigDecimal matchCover) {
        this.matchCover = matchCover;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    private List<ClaimEntity> claimEntityList;
    private List<InvoiceEntity> invoiceEntityList;

    public BigDecimal getCover() {
        return cover;
    }

    public void setCover(BigDecimal cover) {
        this.cover = cover;
    }

    public String getScanMactchStatus() {
        return scanMactchStatus;
    }

    public void setScanMactchStatus(String scanMactchStatus) {
        this.scanMactchStatus = scanMactchStatus;
    }

    public Boolean getEmpty() {
        return isEmpty;
    }

    public void setEmpty(Boolean empty) {
        isEmpty = empty;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
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

    public String getGfTaxNo() {
        return gfTaxNo;
    }

    public void setGfTaxNo(String gfTaxNo) {
        this.gfTaxNo = gfTaxNo;
    }

    public BigDecimal getSettlementamount() {
        return settlementamount;
    }

    public void setSettlementamount(BigDecimal settlementamount) {
        this.settlementamount = settlementamount;
    }

    public int getPoNum() {
        return poNum;
    }

    public void setPoNum(int poNum) {
        this.poNum = poNum;
    }

    public int getClaimNum() {
        return claimNum;
    }

    public void setClaimNum(int claimNum) {
        this.claimNum = claimNum;
    }

    public int getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(int invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getHoststatus() {
        return hoststatus;
    }

    public void setHoststatus(String hoststatus) {
        this.hoststatus = hoststatus;
    }

    public String getReasonForCancel() {
        return reasonForCancel;
    }

    public void setReasonForCancel(String reasonForCancel) {
        this.reasonForCancel = reasonForCancel;
    }

    public String getMatchRemarks() {
        return matchRemarks;
    }

    public void setMatchRemarks(String matchRemarks) {
        this.matchRemarks = matchRemarks;
    }

    public String getMathingSource() {
        return mathingSource;
    }

    public void setMathingSource(String mathingSource) {
        this.mathingSource = mathingSource;
    }

    public String getMatchingType() {
        return matchingType;
    }

    public void setMatchingType(String matchingType) {
        this.matchingType = matchingType;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getPoAmount() {
        return poAmount;
    }

    public void setPoAmount(BigDecimal poAmount) {
        this.poAmount = poAmount;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public List<PoEntity> getPoEntityList() {
        return poEntityList;
    }

    public void setPoEntityList(List<PoEntity> poEntityList) {
        this.poEntityList = poEntityList;
    }

    public List<ClaimEntity> getClaimEntityList() {
        return claimEntityList;
    }

    public void setClaimEntityList(List<ClaimEntity> claimEntityList) {
        this.claimEntityList = claimEntityList;
    }

    public List<InvoiceEntity> getInvoiceEntityList() {
        return invoiceEntityList;
    }

    public void setInvoiceEntityList(List<InvoiceEntity> invoiceEntityList) {
        this.invoiceEntityList = invoiceEntityList;
    }


}
