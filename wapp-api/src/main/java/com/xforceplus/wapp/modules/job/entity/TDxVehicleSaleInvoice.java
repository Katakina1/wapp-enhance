package com.xforceplus.wapp.modules.job.entity;

import java.io.Serializable;
import java.util.Date;

public class TDxVehicleSaleInvoice implements Serializable{
    private Long id;

    private String uuid;

    private String buyerIdNum;

    private String vehicleType;

    private String factoryModel;

    private String productPlace;

    private String certificate;

    private String certificateImport;

    private String inspectionNum;

    private String engineNo;

    private String vehicleNo;

    private String phone;

    private String buyerBank;

    private String taxRate;

    private String taxBureauName;

    private String taxBureauCode;

    private String taxRecords;

    private String limitPeople;

    private String checkStatus;

    private String tonnage;

    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getBuyerIdNum() {
        return buyerIdNum;
    }

    public void setBuyerIdNum(String buyerIdNum) {
        this.buyerIdNum = buyerIdNum == null ? null : buyerIdNum.trim();
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType == null ? null : vehicleType.trim();
    }

    public String getFactoryModel() {
        return factoryModel;
    }

    public void setFactoryModel(String factoryModel) {
        this.factoryModel = factoryModel == null ? null : factoryModel.trim();
    }

    public String getProductPlace() {
        return productPlace;
    }

    public void setProductPlace(String productPlace) {
        this.productPlace = productPlace == null ? null : productPlace.trim();
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate == null ? null : certificate.trim();
    }

    public String getCertificateImport() {
        return certificateImport;
    }

    public void setCertificateImport(String certificateImport) {
        this.certificateImport = certificateImport == null ? null : certificateImport.trim();
    }

    public String getInspectionNum() {
        return inspectionNum;
    }

    public void setInspectionNum(String inspectionNum) {
        this.inspectionNum = inspectionNum == null ? null : inspectionNum.trim();
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo == null ? null : engineNo.trim();
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo == null ? null : vehicleNo.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getBuyerBank() {
        return buyerBank;
    }

    public void setBuyerBank(String buyerBank) {
        this.buyerBank = buyerBank == null ? null : buyerBank.trim();
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate == null ? null : taxRate.trim();
    }

    public String getTaxBureauName() {
        return taxBureauName;
    }

    public void setTaxBureauName(String taxBureauName) {
        this.taxBureauName = taxBureauName == null ? null : taxBureauName.trim();
    }

    public String getTaxBureauCode() {
        return taxBureauCode;
    }

    public void setTaxBureauCode(String taxBureauCode) {
        this.taxBureauCode = taxBureauCode == null ? null : taxBureauCode.trim();
    }

    public String getTaxRecords() {
        return taxRecords;
    }

    public void setTaxRecords(String taxRecords) {
        this.taxRecords = taxRecords == null ? null : taxRecords.trim();
    }

    public String getLimitPeople() {
        return limitPeople;
    }

    public void setLimitPeople(String limitPeople) {
        this.limitPeople = limitPeople == null ? null : limitPeople.trim();
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus == null ? null : checkStatus.trim();
    }

    public String getTonnage() {
        return tonnage;
    }

    public void setTonnage(String tonnage) {
        this.tonnage = tonnage == null ? null : tonnage.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}