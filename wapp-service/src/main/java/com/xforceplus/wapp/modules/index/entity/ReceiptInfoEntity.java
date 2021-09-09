package com.xforceplus.wapp.modules.index.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

public class ReceiptInfoEntity extends AbstractBaseDomain {

    private String address;
    private String zipCode;
    private String tel;
    private String recipients;

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
