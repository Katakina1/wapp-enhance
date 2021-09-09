package com.xforceplus.wapp.modules.posuopei.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ClaimEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 2375783057343236604L;

    private  Integer id;
    /**
     *索赔号
     */
    private  String claimno;

    //jvcode
    private String jvcode;
    /**
     *供应商号
     */
    private  String venderid;
    /**
     *索赔金额
     */
    private BigDecimal claimAmount;
    /**
     *换货号(HOST同步)
     */
    private String exchangeno;
    /**
     *定案日期(HOST同步)
     */
    private Date postdate;

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
     *打印号
     */
    private  String printcode;

    /**
     *匹配关联号
     */
    private  String matchno;


    /**
     *匹配状态：0未匹配、1预匹配（没有发票）、2部分匹配、3完全匹配、4差异匹配、5匹配失败、6取消匹配
     */
    private  String matchstatus;

    /**
     *发票号码
     */
    private  String invoiceno;

    /**
     *凭证号
     */
    private  String certificateno;

    private Boolean isEmpty;

    private String claimType;
    /**
     * 是否逾期40天
     */
    private String ifYq;

    /**
     * 部门号
     */
    private String dept;
    private  String tractionIdSeq;
    private String tractionId;


    private  String seq;
    private String rownumber;
    /**
     *索赔金额
     */
    private BigDecimal newAmount;
    public BigDecimal getNewAmount() {
        return newAmount;
    }

    public void setNewAmount(BigDecimal newAmount) {
        this.newAmount = newAmount;
    }


    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getStoreNbr() {
        return storeNbr;
    }

    public void setStoreNbr(String storeNbr) {
        this.storeNbr = storeNbr;
    }

    private String storeNbr;//门店号
    public String getTractionId() {
        return tractionId;
    }

    public void setTractionId(String tractionId) {
        this.tractionId = tractionId;
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

    public String getIfYq() {
        return ifYq;
    }

    public void setIfYq(String ifYq) {
        this.ifYq = ifYq;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public Boolean getEmpty() {
        return isEmpty;
    }

    public void setEmpty(Boolean empty) {
        isEmpty = empty;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClaimno() {
        return claimno;
    }

    public void setClaimno(String claimno) {
        this.claimno = claimno;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getExchangeno() {
        return exchangeno;
    }

    public void setExchangeno(String exchangeno) {
        this.exchangeno = exchangeno;
    }

    public Date getPostdate() {
        return postdate;
    }

    public void setPostdate(Date postdate) {
        this.postdate = postdate;
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

    public String getMatchstatus() {
        return matchstatus;
    }

    public void setMatchstatus(String matchstatus) {
        this.matchstatus = matchstatus;
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
