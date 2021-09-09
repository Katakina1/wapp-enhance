package com.xforceplus.wapp.modules.posuopei.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.xforceplus.wapp.modules.base.entity.BaseEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author raymond.yan
 */
public class QuestionPaperExcelEntity extends BaseRowModel implements Serializable {
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"分区"},index = 1)
    private String partition;
    @ExcelProperty(value={"采购人姓名"},index = 3)
    private String purchaser;
    @ExcelProperty(value={"购方代码"},index = 4)
    private String jvcode;
    @ExcelProperty(value={"城市"},index = 5)
    private String city;
    private String cityCode;
    @ExcelProperty(value={"供应商编号"},index = 6)
    private String usercode;
    @ExcelProperty(value={"供应商名称"},index = 7)
    private String username;
    @ExcelProperty(value={"采购人联系电话"},index = 8)
    private String telephone;
    @ExcelProperty(value={"部门"},index = 9)
    private String department;
    @ExcelProperty(value={"发票号码"},index = 11)
    private String invoiceNo;
    @ExcelProperty(value={"开票日期"},index = 12)
    private String invoiceDate;
    @ExcelProperty(value={"问题类型"},index = 14)
    private String questionType;
    @ExcelProperty(value={"金额"},index = 13)
    private BigDecimal totalAmount;
    @ExcelProperty(value={"问题原因"},index = 15)
    private String problemCause;
    @ExcelProperty(value={"问题描述"},index = 16)
    private String description;
    @ExcelProperty(value={"审核状态"},index = 18)
    private String checkstatus;
    @ExcelProperty(value={"审核不通过原因"},index = 21)
    private String unPassReason;
    private String src;
    @ExcelProperty(value={"审核日期"},index = 19)
    private String checkDate;
    @ExcelProperty(value={"申请日期"},index = 17)
    private String createdDate;
    @ExcelProperty(value={"驳回日期"},index = 20)
    private String rejectDate;
    @ExcelProperty(value={"批复日期"},index = 22)
    private String replyDate;
    @ExcelProperty(value={"店号"},index = 10)
    private String storeNbr;
    @ExcelProperty(value={"采购流水号"},index = 2)
    private String problemStream;

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String purchaser) {
        this.purchaser = purchaser;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getProblemCause() {
        return problemCause;
    }

    public void setProblemCause(String problemCause) {
        this.problemCause = problemCause;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(String checkstatus) {
        this.checkstatus = checkstatus;
    }

    public String getUnPassReason() {
        return unPassReason;
    }

    public void setUnPassReason(String unPassReason) {
        this.unPassReason = unPassReason;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getRejectDate() {
        return rejectDate;
    }

    public void setRejectDate(String rejectDate) {
        this.rejectDate = rejectDate;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }

    public String getStoreNbr() {
        return storeNbr;
    }

    public void setStoreNbr(String storeNbr) {
        this.storeNbr = storeNbr;
    }

    public String getProblemStream() {
        return problemStream;
    }

    public void setProblemStream(String problemStream) {
        this.problemStream = problemStream;
    }





}
