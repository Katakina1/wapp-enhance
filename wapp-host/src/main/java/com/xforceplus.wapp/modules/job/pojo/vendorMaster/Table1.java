package com.xforceplus.wapp.modules.job.pojo.vendorMaster;

public class Table1 {
    //供应商号
    private String VendorNumber;
    //供应商名称
    private String VendorName;
    //供应商类型
    private String TypeCode;
    //供应商税号
    private String TaxNumber;
    //财务电话号码
    private String FinanceTelephoneNumber1;
    //财务传真号码
    private String FinanceFaxNumber1;
    //财务联系人
    private String FinanceContactPerson1;
    //财务人邮箱
    private String EmailAddress1;
    //邮寄地址
    private String Address1;

    private String bankName;

    private String bankAccount;

    private Integer userid;

    private Integer orgid;

    private String holdStatus;

    private String deletionStatus;

    private String userStatus;

    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVendorNumber() {
        return VendorNumber;
    }

    public void setVendorNumber(String vendorNumber) {
        VendorNumber = vendorNumber;
    }

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        VendorName = vendorName;
    }

    public String getTypeCode() {
        return TypeCode;
    }

    public void setTypeCode(String typeCode) {
        TypeCode = typeCode;
    }

    public String getTaxNumber() {
        return TaxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        TaxNumber = taxNumber;
    }

    public String getFinanceTelephoneNumber1() {
        return FinanceTelephoneNumber1;
    }

    public void setFinanceTelephoneNumber1(String financeTelephoneNumber1) {
        FinanceTelephoneNumber1 = financeTelephoneNumber1;
    }

    public String getFinanceFaxNumber1() {
        return FinanceFaxNumber1;
    }

    public void setFinanceFaxNumber1(String financeFaxNumber1) {
        FinanceFaxNumber1 = financeFaxNumber1;
    }

    public String getFinanceContactPerson1() {
        return FinanceContactPerson1;
    }

    public void setFinanceContactPerson1(String financeContactPerson1) {
        FinanceContactPerson1 = financeContactPerson1;
    }

    public String getEmailAddress1() {
        return EmailAddress1;
    }

    public void setEmailAddress1(String emailAddress1) {
        EmailAddress1 = emailAddress1;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getOrgid() {
        return orgid;
    }

    public void setOrgid(Integer orgid) {
        this.orgid = orgid;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getHoldStatus() {
        return holdStatus;
    }

    public void setHoldStatus(String holdStatus) {
        this.holdStatus = holdStatus;
    }

    public String getDeletionStatus() {
        return deletionStatus;
    }

    public void setDeletionStatus(String deletionStatus) {
        this.deletionStatus = deletionStatus;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
