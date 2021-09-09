package com.xforceplus.wapp.modules.posuopei.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author raymond.yan
 */
public class ReturnScreenEntity extends BaseEntity implements Serializable {
    private Integer id;
    private String code;
    private  String message;
    private String screenName;
    private  String jv;
    private String vender;
    private String inv;
    private String YY;
    private String MM;
    private String DD;
    private String YY1;
    private String MM1;
    private String DD1;
    private  String taxTotal;
    private String InvDue;
    private  String taxRate;
    private  String taxType;
    private  String taxTypeZ;
    private String invTotal;
    private String poNbr;
    private  String payCode;

    //screen II
    private String transaction;
    private String receiver;
    private BigDecimal invPreTaxAmt;
    private  String ifCut;
    private String error;
    public ReturnScreenEntity(String code, String message, String screenName, String jv, String vender, String inv, String YY, String MM, String DD, String YY1, String MM1, String DD1, String taxTotal, String invDue, String taxRate, String taxType,String taxTypeZ, String invTotal,String poNbr,String payCode, String transaction,String receiver,BigDecimal invPreTaxAmt,String ifCut,String error) {
        this.code = code;
        this.message = message;
        this.screenName = screenName;
        this.jv = jv;
        this.vender = vender;
        this.inv = inv;
        this.YY = YY;
        this.MM = MM;
        this.DD = DD;
        this.YY1 = YY1;
        this.MM1 = MM1;
        this.DD1 = DD1;
        this.taxTotal = taxTotal;
        InvDue = invDue;
        this.taxRate = taxRate;
        this.taxType = taxType;
         this.taxTypeZ = taxTypeZ;
        this.invTotal = invTotal;
        this.payCode = payCode;
        this.transaction = transaction;
        this.receiver = receiver;
        this.invPreTaxAmt = invPreTaxAmt;
        this.error=error;
        this.poNbr=poNbr;
        this.ifCut=ifCut;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
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
        return InvDue;
    }

    public void setInvDue(String invDue) {
        InvDue = invDue;
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

    public BigDecimal getInvPreTaxAmt() {
        return invPreTaxAmt;
    }

    public void setInvPreTaxAmt(BigDecimal invPreTaxAmt) {
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


}
