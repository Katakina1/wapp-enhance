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
public class ComprehensiveInvoiceQueryExcelEntity extends BaseRowModel implements Serializable {

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
    @ExcelProperty(value={"价税合计"},index = 15)
    private String totalAmount;

    //备注
    private String remark;

    //发票状态
    @ExcelProperty(value={"发票状态"},index = 10)
    private String invoiceStatus;

    //状态更新时间
    private Date statusUpdateDate;

    //认证日期
    @ExcelProperty(value={"认证日期"},index = 22)
    private String rzhDate;

    //签收日期
    @ExcelProperty(value={"签收日期"},index = 20)
    private String qsDate;

    //税款所属期
    @ExcelProperty(value={"税款所属期"},index = 34)
    private String rzhBelongDate;

    //认证状态
    @ExcelProperty(value={"认证状态"},index = 21)
    private String rzhYesorno;

    //签收类型
    @ExcelProperty(value={"签收方式"},index = 19)
    private String qsType;

    //签收状态
    @ExcelProperty(value={"签收状态"},index = 18)
    private String qsStatus;

    //认证结果
    @ExcelProperty(value={"认证处理状态"},index = 35)
    private String authStatus;

    //凭证号
    @ExcelProperty(value={"凭证号"},index = 12)
    private  String certificateNo;

    //匹配状态
    @ExcelProperty(value={"匹配状态"},index = 16)
    private  String dxhyMatchStatus;

    //匹配日期
    @ExcelProperty(value={"匹配日期"},index = 17)
    private String matchDate;

    //host状态
    @ExcelProperty(value={"沃尔玛状态"},index = 23)
    private  String hostStatus;
    //扫描流水号
    @ExcelProperty(value={"扫描流水号"},index = 24)
    private  String scanningSeriano;
    //装订册号
    @ExcelProperty(value={"装订册号"},index = 25)
    private  String bbindingno;
    //装箱号
    @ExcelProperty(value={"装箱号"},index = 26)
    private  String packingno;
    @ExcelProperty(value={"存储地"},index = 27)
    private  String packingAddress;
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
    @ExcelProperty(value={"JV"},index = 13)
    private String  jvCode;
    //companyCodeJV
    @ExcelProperty(value={"公司代码"},index = 14)
    private String companyCode;

    private String createDate;
    private String tpStatus;
    private String tpDate;

    private String redticketDataSerialNumber;

    private String redTotalAmount;
    @ExcelProperty(value={"付款日期"},index = 37)
    private String paymentDate;

    private String redticketCreationTime;
    //发票流程类型
    @ExcelProperty(value={"业务类型"},index = 36)
    private String flowType;
    @ExcelProperty(value={"借阅人"},index = 28)
    private String borrowUser;
    @ExcelProperty(value={"借阅原因"},index = 29)
    private String borrowReason;
    @ExcelProperty(value={"借阅部门"},index = 30)
    private String borrowDept;
    @ExcelProperty(value={"借阅时间"},index = 31)
    private String borrowDate;
    @ExcelProperty(value={"归还人"},index = 32)
    private String borrowReturnUser;
    @ExcelProperty(value={"归还时间"},index = 33)
    private String borrowReturnDate;
    private String scanMatchStatus;

    private String epsNo;

    private String confirmReason;

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

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getRedTotalAmount() {
        return redTotalAmount;
    }

    public void setRedTotalAmount(String redTotalAmount) {
        this.redTotalAmount = redTotalAmount;
    }

    public String getRedticketCreationTime() {
        return redticketCreationTime;
    }

    public void setRedticketCreationTime(String redticketCreationTime) {
        this.redticketCreationTime = redticketCreationTime;
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

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getRzhBelongDate() {
        return rzhBelongDate;
    }

    public void setRzhBelongDate(String rzhBelongDate) {
        this.rzhBelongDate = rzhBelongDate;
    }

    public String getRzhYesorno() {
        return rzhYesorno;
    }

    public void setRzhYesorno(String rzhYesorno) {
        this.rzhYesorno = rzhYesorno;
    }

    public String getQsType() {
        return qsType;
    }

    public void setQsType(String qsType) {
        this.qsType = qsType;
    }

    public String getQsStatus() {
        return qsStatus;
    }

    public void setQsStatus(String qsStatus) {
        this.qsStatus = qsStatus;
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getDxhyMatchStatus() {
        return dxhyMatchStatus;
    }

    public void setDxhyMatchStatus(String dxhyMatchStatus) {
        this.dxhyMatchStatus = dxhyMatchStatus;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getHostStatus() {
        return hostStatus;
    }

    public void setHostStatus(String hostStatus) {
        this.hostStatus = hostStatus;
    }

    public String getScanningSeriano() {
        return scanningSeriano;
    }

    public void setScanningSeriano(String scanningSeriano) {
        this.scanningSeriano = scanningSeriano;
    }

    public String getBbindingno() {
        return bbindingno;
    }

    public void setBbindingno(String bbindingno) {
        this.bbindingno = bbindingno;
    }

    public String getPackingno() {
        return packingno;
    }

    public void setPackingno(String packingno) {
        this.packingno = packingno;
    }


    public Date getStatusUpdateDate() {
        if(statusUpdateDate == null){
            return null;
        }
        return (Date) statusUpdateDate.clone();
    }

    public void setStatusUpdateDate(Date statusUpdateDate) {
        if(statusUpdateDate == null){
            this.statusUpdateDate = null;
        }else {
            this.statusUpdateDate = (Date) statusUpdateDate.clone();
        }
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

    public String getRzhDate() {
        return rzhDate;
    }

    public void setRzhDate(String rzhDate) {
        this.rzhDate = rzhDate;
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
