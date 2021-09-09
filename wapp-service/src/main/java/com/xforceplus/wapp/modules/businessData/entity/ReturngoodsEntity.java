package com.xforceplus.wapp.modules.businessData.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Adil.Xu
 */
public class ReturngoodsEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3362830731299248353L;
    //ID
    private Long id;

    //退货号
    private String returnGoodsCode;

    //供应商号
    private String supplierAssociation;

    //退货时间
    private Date returnGoodsDate;

    //退货金额
    private BigDecimal returnGoodsAmount;

    //退货成本金额
    private BigDecimal returncostAmount;

    //退货状态
    private String returnGoodsStatus;

    //如果被红冲，产生序列号
    private String redticketDataSerialNumber;

    private List<ReturngoodsEntity> returnGoods;

    public Long getId() { return id; }

    public String getReturnGoodsCode() { return returnGoodsCode; }

    public String getSupplierAssociation() { return supplierAssociation; }

    public Date getReturnGoodsDate() { return returnGoodsDate; }

    public BigDecimal getReturnGoodsAmount() { return returnGoodsAmount; }

    public BigDecimal getReturncostAmount() { return returncostAmount; }

    public String getReturnGoodsStatus() { return returnGoodsStatus; }

    public String getRedticketDataSerialNumber() { return redticketDataSerialNumber; }

    public void setId(Long id) { this.id = id; }

    public void setReturnGoodsCode(String returnGoodsCode) { this.returnGoodsCode = returnGoodsCode; }

    public void setSupplierAssociation(String supplierAssociation) { this.supplierAssociation = supplierAssociation; }

    public void setReturnGoodsDate(Date returnGoodsDate) { this.returnGoodsDate = returnGoodsDate; }

    public void setReturnGoodsAmount(BigDecimal returnGoodsAmount) { this.returnGoodsAmount = returnGoodsAmount; }

    public void setReturncostAmount(BigDecimal returncostAmount) { this.returncostAmount = returncostAmount; }

    public void setReturnGoodsStatus(String returnGoodsStatus) { this.returnGoodsStatus = returnGoodsStatus; }

    public void setRedticketDataSerialNumber(String redticketDataSerialNumber) { this.redticketDataSerialNumber = redticketDataSerialNumber; }

    public List<ReturngoodsEntity> getReturnGoods() {
        return returnGoods;
    }

    public void setReturnGoods(List<ReturngoodsEntity> returnGoods) {
        this.returnGoods = returnGoods;
    }
}
