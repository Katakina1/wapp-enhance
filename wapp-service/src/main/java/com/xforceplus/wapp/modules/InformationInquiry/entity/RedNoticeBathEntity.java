package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.io.Serializable;

/**
 * Created by 1 on 2018/11/20 20:53
 */
public class RedNoticeBathEntity extends AbstractBaseDomain implements Serializable {
    private int indexNo;//序号
    private String xfName;//销方名称
    private String xfTaxno;//销方税号
    private String gfName;//购方名称
    private String gfTaxno;//购方税号
    private String redTicketDataSerialNumber;//序列号
    private String amount;//金额
    private String taxRate;//税率
    private String taxAmount;//税额
    private String tkDate;//填开日期
    private String redNoticeNumber;//红字通知单号
    private String createDate;//创建日期
    private String updateDate;//修改日期
    private String updatePersion;//操作人
    private String redTicketType;//红票类型
    private String uuid;//uuid

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRedTicketType() {
        return redTicketType;
    }

    public void setRedTicketType(String redTicketType) {
        this.redTicketType = redTicketType;
    }

    public RedNoticeBathEntity() {

    }

    public int getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(int indexNo) {
        this.indexNo = indexNo;
    }

    public String getXfName() {
        return xfName;
    }

    public void setXfName(String xfName) {
        this.xfName = xfName;
    }

    public String getXfTaxno() {
        return xfTaxno;
    }

    public void setXfTaxno(String xfTaxno) {
        this.xfTaxno = xfTaxno;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getGfTaxno() {
        return gfTaxno;
    }

    public void setGfTaxno(String gfTaxno) {
        this.gfTaxno = gfTaxno;
    }

    public String getRedTicketDataSerialNumber() {
        return redTicketDataSerialNumber;
    }

    public void setRedTicketDataSerialNumber(String redTicketDataSerialNumber) {
        this.redTicketDataSerialNumber = redTicketDataSerialNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTkDate() {
        return tkDate;
    }

    public void setTkDate(String tkDate) {
        this.tkDate = tkDate;
    }

    public String getRedNoticeNumber() {
        return redNoticeNumber;
    }

    public void setRedNoticeNumber(String redNoticeNumber) {
        this.redNoticeNumber = redNoticeNumber;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatePersion() {
        return updatePersion;
    }

    public void setUpdatePersion(String updatePersion) {
        this.updatePersion = updatePersion;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
