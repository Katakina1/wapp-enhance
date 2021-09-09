package com.xforceplus.wapp.modules.posuopei.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author raymond.yan
 */
public class WriterScreenDataEntity  implements Serializable {

    private String jv;
    private String vender;
    private String inv;
    private String YY;
    private String MM;
    private String DD;
    private String YY1;
    private String MM1;
    private String DD1;
    private String taxTotal;
    private String invDue;
    private String taxRate;
    private String taxType;
    private String taxTypeZ;
    private String invTotal;
    private String poNbr;
    private String payCode;
    private String transaction;
    private String receiver;
    private String invPreTaxAmt;
    private String ifCut;
    private String error;
    private String balance;
    private String ifFapr;
    private String cover;
    private String seq;
    private List<LoopEntity> comments;

    public String getBalance() {
        return balance;
    }
    public void setBalance(String balance) {
        this.balance = balance;
    }
    public List<LoopEntity> getComments() {
        return comments;
    }

    public void setComments(List<LoopEntity> comments) {
        this.comments = comments;
    }
    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getIfFapr() {
        return ifFapr;
    }

    public void setIfFapr(String ifFapr) {
        this.ifFapr = ifFapr;
    }

    public WriterScreenDataEntity() {
    }

    public String getJv() {
        return jv;
    }

    public void setJv(String jv) {
        this.jv = jv;
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender;
    }

    public String getInv() {
        return inv;
    }

    public void setInv(String inv) {
        this.inv = inv;
    }

    public String getYY() {
        return YY;
    }

    public void setYY(String YY) {
        this.YY = YY;
    }

    public String getMM() {
        return MM;
    }

    public void setMM(String MM) {
        this.MM = MM;
    }

    public String getDD() {
        return DD;
    }

    public void setDD(String DD) {
        this.DD = DD;
    }

    public String getYY1() {
        return YY1;
    }

    public void setYY1(String YY1) {
        this.YY1 = YY1;
    }

    public String getMM1() {
        return MM1;
    }

    public void setMM1(String MM1) {
        this.MM1 = MM1;
    }

    public String getDD1() {
        return DD1;
    }

    public void setDD1(String DD1) {
        this.DD1 = DD1;
    }

    public String getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(String taxTotal) {
        this.taxTotal = taxTotal;
    }

    public String getInvDue() {
        return invDue;
    }

    public void setInvDue(String invDue) {
        this.invDue = invDue;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getTaxTypeZ() {
        return taxTypeZ;
    }

    public void setTaxTypeZ(String taxTypeZ) {
        this.taxTypeZ = taxTypeZ;
    }

    public String getInvTotal() {
        return invTotal;
    }

    public void setInvTotal(String invTotal) {
        this.invTotal = invTotal;
    }

    public String getPoNbr() {
        return poNbr;
    }

    public void setPoNbr(String poNbr) {
        this.poNbr = poNbr;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getInvPreTaxAmt() {
        return invPreTaxAmt;
    }

    public void setInvPreTaxAmt(String invPreTaxAmt) {
        this.invPreTaxAmt = invPreTaxAmt;
    }

    public String getIfCut() {
        return ifCut;
    }

    public void setIfCut(String ifCut) {
        this.ifCut = ifCut;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public WriterScreenDataEntity(String jv, String vender, String inv, String YY, String MM, String DD, String taxTotal, String taxRate, String taxType, String invTotal, String payCode, String transaction, String receiver, String invPreTaxAmt,String ifCut,String error,String ifFapr,String cover) {
        this.jv = jv;
        this.vender = vender;
        this.inv = inv;
        this.YY = YY;
        this.MM = MM;
        this.DD = DD;
        this.taxTotal = taxTotal;
        this.taxRate = taxRate;
        this.taxType = taxType;
        this.invTotal = invTotal;
        this.payCode = payCode;
        this.transaction = transaction;
        this.receiver = receiver;
        this.invPreTaxAmt = invPreTaxAmt;
        this.ifCut = ifCut;
        this.ifFapr = ifFapr;
        this.error = error;
        this.cover = cover;
    }

}
