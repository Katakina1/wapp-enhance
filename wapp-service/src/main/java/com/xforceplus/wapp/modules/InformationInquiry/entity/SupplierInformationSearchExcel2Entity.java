package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;
import java.util.List;

public class SupplierInformationSearchExcel2Entity extends BaseRowModel implements Serializable {





    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String indexNo;
    //JV号
    @ExcelProperty(value={"JV代码"},index = 1)
    private String orgcode;

    //机构名称
    @ExcelProperty(value={"合资公司名称"},index = 2)
    private String orgName;
    @ExcelProperty(value={"税务登记号"},index = 3)
    private String taxno;
    //联系地址
    @ExcelProperty(value={"公司地址"},index = 4)
    private String address;
    //电话号码
    @ExcelProperty(value={"电话"},index = 5)
    private String phone;
    //开户行
    @ExcelProperty(value={"开户行"},index = 6)
    private String bank;
    //银行帐号
    @ExcelProperty(value={"账号"},index = 7)
    private String account;
    @ExcelProperty(value={"备注"},index = 8)
    private String remark;//备注
    //门店号
    @ExcelProperty(value={"成本中心"},index = 9)
    private String storeNumber;

    public String getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getTaxno() {
        return taxno;
    }

    public void setTaxno(String taxno) {
        this.taxno = taxno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
