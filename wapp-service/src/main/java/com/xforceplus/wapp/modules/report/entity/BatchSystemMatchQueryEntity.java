package com.xforceplus.wapp.modules.report.entity;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 匹配表
 */
public class BatchSystemMatchQueryEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String jv;//购方名称
    private String vender;//供应商编码
    private String invTotal;//发票金额
    private String code;//状态
    private String inv;//发票号
    private String yy;//年
    private String mm;//月
    private String dd;//日
    private String yy1;//
    private String mm1;//
    private String dd1;//
    private String taxRate;//发票税率
    private Date createDate;//导入时间
    private String  matchNo;


    //
    private  String taxAmount;
    private String taxType;
    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTaxType() {
        if(StringUtils.isBlank(taxType)){
            return "01";
        }else {
            return "04";
        }

    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getInvTotal() {
        return invTotal;
    }

    public void setInvTotal(String invTotal) {
        this.invTotal = invTotal;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInv() {
        return inv;
    }

    public void setInv(String inv) {
        this.inv = inv;
    }

    public String getYy() {
        return yy;
    }

    public void setYy(String yy) {
        this.yy = yy;
    }

    public String getMm() {
        return mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
    }

    public String getDd() {
        return dd;
    }

    public void setDd(String dd) {
        this.dd = dd;
    }

    public String getYy1() {
        return yy1;
    }

    public void setYy1(String yy1) {
        this.yy1 = yy1;
    }

    public String getMm1() {
        return mm1;
    }

    public void setMm1(String mm1) {
        this.mm1 = mm1;
    }

    public String getDd1() {
        return dd1;
    }

    public void setDd1(String dd1) {
        this.dd1 = dd1;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMatchNo() {
        return matchNo;
    }

    public void setMatchNo(String matchNo) {
        this.matchNo = matchNo;
    }
}
