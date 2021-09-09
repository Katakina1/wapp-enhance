package com.xforceplus.wapp.modules.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 抵账表实体(发票签收)
 */
@Getter
@Setter
public class DirectAuthQueryExcelEntity extends BaseRowModel implements Serializable {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;

    //税率
    private BigDecimal taxRate;

    //UUID
    private String uuid;

    //发票类型
    private String invoiceType;

    //发票代码
    @ExcelProperty(value={"发票代码"},index = 1)
    private String invoiceCode;

    //发票号码
    @ExcelProperty(value={"发票号码"},index = 2)
    private String invoiceNo;

    //开票日期
    @ExcelProperty(value={"开票日期"},index = 3)
    private String invoiceDate;

    //购方税号
    @ExcelProperty(value={"购方税号"},index = 4)
    private String gfTaxNo;

    //购方名称
    @ExcelProperty(value={"购方名称"},index = 5)
    private String gfName;

    //销方税号
    @ExcelProperty(value={"销方税号"},index = 6)
    private String xfTaxNo;

    //销方名称
    @ExcelProperty(value={"销方名称"},index = 7)
    private String xfName;

    //金额
    @ExcelProperty(value={"金额"},index = 8)
    private String invoiceAmount;

    //税额
    @ExcelProperty(value={"税额"},index = 9)
    private String taxAmount;

    //税价合计
    @ExcelProperty(value={"价税合计"},index = 10)
    private String totalAmount;
    //备注
    private String remark;

    //供应商号
    @ExcelProperty(value={"供应商号"},index = 11)
    private String venderId;
    //供应商名称
    private String venderName;
    //扫描匹配日期
    private Date scanMatchDate;
    //数据发票提交统计用，非数据库字段
    private int countNum;
    //jvcode
    @ExcelProperty(value={"JV"},index = 12)
    private String  jvCode;
    //companyCodeJV
    @ExcelProperty(value={"公司代码"},index = 13)
    private String companyCode;
    //签收日期
    @ExcelProperty(value={"签收日期"},index = 14)
    private String qsDate;
    //是否确认
    @ExcelProperty(value={"是否确认(Y/N)"},index = 15)
    private String confirm;

    private String createDate;
    private String tpStatus;
    private String tpDate;

    private String redticketDataSerialNumber;

    private String redTotalAmount;

    private String flowType;
    private String scanMatchStatus;

    private String epsNo;

    private String confirmReason;
    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
    public String getConfirmReason() {
        return confirmReason;
    }

    public void setConfirmReason(String confirmReason) {
        this.confirmReason = confirmReason;
    }

    public String getScanMatchStatus() {
        return scanMatchStatus;
    }

    public void setScanMatchStatus(String scanMatchStatus) {
        this.scanMatchStatus = scanMatchStatus;
    }

    public String getHostDate() {
        return hostDate;
    }

    public void setHostDate(String hostDate) {
        this.hostDate = hostDate;
    }

    private String hostDate;

    public String getRedticketDataSerialNumber() {
        return redticketDataSerialNumber;
    }

    public void setRedticketDataSerialNumber(String redticketDataSerialNumber) {
        this.redticketDataSerialNumber = redticketDataSerialNumber;
    }


    public String getRedTotalAmount() {
        return redTotalAmount;
    }

    public void setRedTotalAmount(String redTotalAmount) {
        this.redTotalAmount = redTotalAmount;
    }



    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getJvCode() {
        return jvCode;
    }

    public void setJvCode(String jvCode) {
        this.jvCode = jvCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public void setCountNum(int countNum) {
        this.countNum = countNum;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }



    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
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

    public String getGfTaxNo() {
        return gfTaxNo;
    }

    public void setGfTaxNo(String gfTaxNo) {
        this.gfTaxNo = gfTaxNo;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getXfTaxNo() {
        return xfTaxNo;
    }

    public void setXfTaxNo(String xfTaxNo) {
        this.xfTaxNo = xfTaxNo;
    }

    public String getXfName() {
        return xfName;
    }

    public void setXfName(String xfName) {
        this.xfName = xfName;
    }



    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getVenderName() {
    	return venderName;
    }
    
    public void setVenderName(String venderName) {
    	this.venderName = venderName;
    }
    
    public Date getScanMatchDate() {
    	return scanMatchDate;
    }
    
    public void setScanMatchDate(Date date) {
    	 this.scanMatchDate = date;
    }
    
    public int getCountNum() {
    	return countNum;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getEpsNo() {
        return epsNo;
    }

    public void setEpsNo(String epsNo) {
        this.epsNo = epsNo;
    }

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getQsDate() {
        return qsDate;
    }

    public void setQsDate(String qsDate) {
        this.qsDate = qsDate;
    }

    public String getTpStatus() {
        return tpStatus;
    }

    public void setTpStatus(String tpStatus) {
        this.tpStatus = tpStatus;
    }

    public String getTpDate() {
        return tpDate;
    }

    public void setTpDate(String tpDate) {
        this.tpDate = tpDate;
    }
}
