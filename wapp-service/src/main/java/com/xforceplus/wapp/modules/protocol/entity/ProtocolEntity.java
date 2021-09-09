package com.xforceplus.wapp.modules.protocol.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.util.Date;

public class ProtocolEntity extends AbstractBaseDomain {

    //序号
    private String seq;

    //协议号码
    private String protocolNo;

    //供应商号
    private String venderId;

    //供应商名称
    private String venderName;

    //部门号
    private String deptNo;

    //扣款项目
    private String payItem;

    //付款公司供码
    private String payCompanyCode;

    //金额
    private BigDecimal amount;

    //协议状态(0-未审批,1-审批完成)
    private String protocolStatus;

    //定案日期
    private Date caseDate;

    //付款日期
    private Date payDate;

    //付款公司名称
    private String payCompany;

    //原因
    private String reason;

    //号码
    private String number;

    //号码解释
    private String numberDesc;

    //店号
    private String store;

    //金额
    private BigDecimal detailAmount;

    //sheet行
    private Integer row;

    //上传日期
    private Date uploadDate;

    //导入失败原因
    private String failureReason;

    //创建人code
    private String userCode;

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (seq==null?0:seq.hashCode());
        result = 31 * result + (protocolNo==null?0:protocolNo.hashCode());
        result = 31 * result + (venderId==null?0:venderId.hashCode());
        result = 31 * result + (venderName==null?0:venderName.hashCode());
        result = 31 * result + (deptNo==null?0:deptNo.hashCode());
        result = 31 * result + (payItem==null?0:payItem.hashCode());
        result = 31 * result + (payCompanyCode==null?0:payCompanyCode.hashCode());
        result = 31 * result + (amount==null?0:amount.hashCode());
        result = 31 * result + (protocolStatus==null?0:protocolStatus.hashCode());
        result = 31 * result + (caseDate==null?0:caseDate.hashCode());
        result = 31 * result + (payDate==null?0:payDate.hashCode());
        result = 31 * result + (reason==null?0:reason.hashCode());
        result = 31 * result + (number==null?0:number.hashCode());
        result = 31 * result + (numberDesc==null?0:numberDesc.hashCode());
        result = 31 * result + (store==null?0:store.hashCode());
        result = 31 * result + (detailAmount==null?0:detailAmount.hashCode());
        result = 31 * result + (userCode==null?0:userCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ProtocolEntity o = (ProtocolEntity) object;

        return this.toString().equals(o.toString());

    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

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

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public BigDecimal getDetailAmount() {
        return detailAmount;
    }

    public void setDetailAmount(BigDecimal detailAmount) {
        this.detailAmount = detailAmount;
    }

    public String getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
    }

    public String getPayCompany() {
        return payCompany;
    }

    public void setPayCompany(String payCompany) {
        this.payCompany = payCompany;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
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

    public String getPayItem() {
        return payItem;
    }

    public void setPayItem(String payItem) {
        this.payItem = payItem;
    }

    public String getPayCompanyCode() {
        return payCompanyCode;
    }

    public void setPayCompanyCode(String payCompanyCode) {
        this.payCompanyCode = payCompanyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getProtocolStatus() {
        return protocolStatus;
    }

    public void setProtocolStatus(String protocolStatus) {
        this.protocolStatus = protocolStatus;
    }

    public Date getCaseDate() {
        return caseDate;
    }

    public void setCaseDate(Date caseDate) {
        this.caseDate = caseDate;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("seq", seq)
                .add("protocolNo", protocolNo)
                .add("venderId", venderId)
                .add("venderName", venderName)
                .add("deptNo", deptNo)
                .add("payItem", payItem)
                .add("payCompanyCode", payCompanyCode)
                .add("amount", amount)
                .add("protocolStatus", protocolStatus)
                .add("caseDate", caseDate)
                .add("payDate", payDate)
                .add("payCompany", payCompany)
                .add("reason", reason)
                .add("number", number)
                .add("numberDesc", numberDesc)
                .add("store", store)
                .add("detailAmount", detailAmount)
                .add("uploadDate", uploadDate)
                .add("failureReason", failureReason)
                .add("userCode", userCode)
                .toString();
    }
}
