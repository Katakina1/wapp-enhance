package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class ReturnGoodsEntity extends AbstractBaseDomain implements Serializable {
    private static final long serialVersionUID = -7041364386283157524L;


    //退货号
    private String returnGoodsCode;

    //供应商号
    private String supplierAssociation;

    //退货时间
    private Date returnGoodsDate;

    //退货金额
    private BigDecimal returnGoodsAmount;

    //退货成本金额
    private BigDecimal returnCostAmount;

    //退货状态
    private String returnGoodsStatus;

    //如果被红冲，产生序列号
    private String redTicketDataSerialNumber;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public BigDecimal getReturnCostAmount() {
        return returnCostAmount;
    }

    public void setReturnCostAmount(BigDecimal returnCostAmount) {
        this.returnCostAmount = returnCostAmount;
    }

    public String getRedTicketDataSerialNumber() {
        return redTicketDataSerialNumber;
    }

    public void setRedTicketDataSerialNumber(String redTicketDataSerialNumber) {
        this.redTicketDataSerialNumber = redTicketDataSerialNumber;
    }

    public String getReturnGoodsCode() { return returnGoodsCode; }

    public String getSupplierAssociation() { return supplierAssociation; }

    public Date getReturnGoodsDate() { return returnGoodsDate; }

    public BigDecimal getReturnGoodsAmount() { return returnGoodsAmount; }


    public String getReturnGoodsStatus() { return returnGoodsStatus; }



    public void setReturnGoodsCode(String returnGoodsCode) { this.returnGoodsCode = returnGoodsCode; }

    public void setSupplierAssociation(String supplierAssociation) { this.supplierAssociation = supplierAssociation; }

    public void setReturnGoodsDate(Date returnGoodsDate) { this.returnGoodsDate = returnGoodsDate; }

    public void setReturnGoodsAmount(BigDecimal returnGoodsAmount) { this.returnGoodsAmount = returnGoodsAmount; }


    public void setReturnGoodsStatus(String returnGoodsStatus) { this.returnGoodsStatus = returnGoodsStatus; }


    @Override
    public Boolean isNullObject() {
        return null;
    }
}
