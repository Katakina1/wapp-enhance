package com.xforceplus.wapp.modules.fixed.entity;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * t_dx_fixed_assets_order
 * @author
 */
public class OrderEntity implements Serializable {
    private Long id;

    /**
     * 供应商号
     */
    private String venderid;


    /**
     * 供应商类型
     */
    private String orgType;

    /**
     * 供应商(非数据库字段,关联表)
     */
    private OrganizationEntity organizationEntity;

    /**
     * jv代码
     */
    private String jvcode;

    /**
     * jv名称
     */
    private String jvname;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单明细号
     */
    private String orderDetailNo;

    /**
     * 公司代码
     */
    private String companyCode;

    /**
     * amount价税合计
     */
    private BigDecimal amount;

    /**
     * 收货未税金额
     */
    private BigDecimal shamount;

    /**
     * 匹配状态 0-未匹配  1-已匹配
     */
    private String matchStatus;


    /**
     * sap匹配状态 0-未匹配  1-已匹配
     */
    private String sapStatus;

    /**
     * 创建日期
     */
    private String createDate;

    /**
     * 修改日期
     */
    private String updateDate;

    //订单日期
    private String orderDate;

    public String getSapStatus() {
        return sapStatus;
    }

    public void setSapStatus(String sapStatus) {
        this.sapStatus = sapStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    private static final long serialVersionUID = 1L;

    public OrganizationEntity getOrganizationEntity() {
        return organizationEntity;
    }



    public void setOrganizationEntity(OrganizationEntity organizationEntity) {
        this.organizationEntity = organizationEntity;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getJvname() {
        return jvname;
    }

    public void setJvname(String jvname) {
        this.jvname = jvname;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDetailNo() {
        return orderDetailNo;
    }

    public void setOrderDetailNo(String orderDetailNo) {
        this.orderDetailNo = orderDetailNo;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
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

    public BigDecimal getShamount() {
        return shamount;
    }

    public void setShamount(BigDecimal shamount) {
        this.shamount = shamount;
    }
}