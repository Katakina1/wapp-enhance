package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import com.xforceplus.wapp.modules.businessData.entity.AgreementEntity;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsEntity;

import java.math.BigDecimal;
import java.util.List;

public class GenerateRedRush extends AbstractBaseDomain {

    private static final long serialVersionUID = -8166545048859720357L;
    private List<InvoiceDetail> invoiceDetails;//发票明细集合
    private List<InvoiceDetail> redRushDetails;//红冲明细集合
    private List<ReturngoodsEntity> returnGoods;//退货集合
    private List<ProtocolEntity> agreementEntities;//协议集合
    private BigDecimal sumRedRushAmount;//红冲总金额
    private String businessType;//业务类型
    private String orgcode;//JV

    public List<InvoiceDetail> getInvoiceDetails() {
        return invoiceDetails;
    }

    public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }

    public List<InvoiceDetail> getRedRushDetails() {
        return redRushDetails;
    }

    public void setRedRushDetails(List<InvoiceDetail> redRushDetails) {
        this.redRushDetails = redRushDetails;
    }

    public List<ReturngoodsEntity> getReturnGoods() {
        return returnGoods;
    }

    public void setReturnGoods(List<ReturngoodsEntity> returnGoods) {
        this.returnGoods = returnGoods;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public BigDecimal getSumRedRushAmount() {
        return sumRedRushAmount;
    }

    public void setSumRedRushAmount(BigDecimal sumRedRushAmount) {
        this.sumRedRushAmount = sumRedRushAmount;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public List<ProtocolEntity> getAgreementEntities() {
        return agreementEntities;
    }

    public void setAgreementEntities(List<ProtocolEntity> agreementEntities) {
        this.agreementEntities = agreementEntities;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
