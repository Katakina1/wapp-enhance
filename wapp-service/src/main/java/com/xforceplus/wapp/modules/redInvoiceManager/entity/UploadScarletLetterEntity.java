package com.xforceplus.wapp.modules.redInvoiceManager.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 *
 */
public class UploadScarletLetterEntity implements Serializable {

    //ID
    private Long id;

    //开红通门店号
    private  String store;

    //开票月份
    private  String makeoutDate;

    //上传日期
    private  String createDate;

    //红字通知号
    private  String redLetterNotice;

    //税率
    private BigDecimal taxRate;

    //发票类型
    private String invoiceType;

    //开票日期
    private String invoiceDate;

    //开票方名称
    private String buyerName;

    //金额
    private BigDecimal invoiceAmount;

    //税额
    private BigDecimal taxAmount;

    //机构类型
    private String orgType;

    //序列号
    private String serialNumber;

    private String totalCount;

    private String fileName;

    //红字通知单状态
    private String redLetterStatus;

    //文件路径
    private String filePath;
    //文件类型
    private String fileType;
    //本地文件名称
    private String localFileName;
    //纳税人识别号
    private String taxNo;

    //jvCode
    private String jvCode;

    //公司代码
    private String companyCode;

    public String getSpfName() {
        return spfName;
    }

    public void setSpfName(String spfName) {
        this.spfName = spfName;
    }

    //收票方名称
    private String spfName;


    private String schemaLabel;

    private String rownumber;
    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getMakeoutDate() {
        return makeoutDate;
    }

    public void setMakeoutDate(String makeoutDate) {
        this.makeoutDate = makeoutDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getRedLetterNotice() {
        return redLetterNotice;
    }

    public void setRedLetterNotice(String redLetterNotice) {
        this.redLetterNotice = redLetterNotice;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getSchemaLabel() {
        return schemaLabel;
    }

    public void setSchemaLabel(String schemaLabel) {
        this.schemaLabel = schemaLabel;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRedLetterStatus() {
        return redLetterStatus;
    }

    public void setRedLetterStatus(String redLetterStatus) {
        this.redLetterStatus = redLetterStatus;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
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
}
