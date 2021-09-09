package com.xforceplus.wapp.modules.protocol.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ProtocolExcelEntity  extends BaseRowModel   implements Serializable {

    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;

    //供应商号
    @ExcelProperty(value={"供应商号"},index = 1)
    private String venderId;

    //供应商名称
    @ExcelProperty(value={"供应商名称"},index = 2)
    private String venderName;

    //部门号
    @ExcelProperty(value={"部门号"},index = 3)
    private String deptNo;

    @ExcelProperty(value={"顺序号"},index = 4)
    private String seq;

    //协议号码
    @ExcelProperty(value={"协议号"},index = 5)
    private String protocolNo;



    //扣款项目
    @ExcelProperty(value={"扣款项目"},index = 6)
    private String payItem;
    //付款公司名称
    @ExcelProperty(value={"扣款公司名称"},index = 7)
    private String payCompany;


    //金额
    @ExcelProperty(value={"扣款金额"},index = 8)
    private String amount;

    //协议状态(0-未审批,1-审批完成)
    @ExcelProperty(value={"协议状态"},index = 9)
    private String protocolStatus;

    //定案日期
    @ExcelProperty(value={"协议定案日期"},index = 10)
    private String caseDate;

    //付款日期
    @ExcelProperty(value={"付款日期"},index = 11)
    private String payDate;

    //原因
    @ExcelProperty(value={"原因"},index = 12)
    private String reason;

    //号码
    @ExcelProperty(value={"号码"},index = 13)
    private String number;

    //号码解释
    @ExcelProperty(value={"号码解释"},index = 14)
    private String numberDesc;

    //扣款金额
    @ExcelProperty(value={"扣款金额"},index = 15)
    private String detailAmount;

    //店号
    @ExcelProperty(value={"店号"},index = 16)
    private String store;
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumberDesc() {
        return numberDesc;
    }

    public void setNumberDesc(String numberDesc) {
        this.numberDesc = numberDesc;
    }

    public String getDetailAmount() {
        return detailAmount;
    }

    public void setDetailAmount(String detailAmount) {
        this.detailAmount = detailAmount;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }


    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
    }

    public String getPayItem() {
        return payItem;
    }

    public void setPayItem(String payItem) {
        this.payItem = payItem;
    }

    public String getPayCompany() {
        return payCompany;
    }

    public void setPayCompany(String payCompany) {
        this.payCompany = payCompany;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getProtocolStatus() {
        return protocolStatus;
    }

    public void setProtocolStatus(String protocolStatus) {
        this.protocolStatus = protocolStatus;
    }

    public String getCaseDate() {
        return caseDate;
    }

    public void setCaseDate(String caseDate) {
        this.caseDate = caseDate;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
}
