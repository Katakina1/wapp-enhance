package com.xforceplus.wapp.modules.collect.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 *
 * 查验发票响应
 * @author Colin.hu
 * @date 4/17/2018
 */
@Getter @Setter @ToString
public class ResponseInvoice {

    public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultTip() {
		return resultTip;
	}

	public void setResultTip(String resultTip) {
		this.resultTip = resultTip;
	}

	public String getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(String checkCount) {
		this.checkCount = checkCount;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getBuyerTaxNo() {
		return buyerTaxNo;
	}

	public void setBuyerTaxNo(String buyerTaxNo) {
		this.buyerTaxNo = buyerTaxNo;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
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

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
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

	public String getMachineNo() {
		return machineNo;
	}

	public void setMachineNo(String machineNo) {
		this.machineNo = machineNo;
	}

	public String getSalerTaxNo() {
		return salerTaxNo;
	}

	public void setSalerTaxNo(String salerTaxNo) {
		this.salerTaxNo = salerTaxNo;
	}

	public String getSalerName() {
		return salerName;
	}

	public void setSalerName(String salerName) {
		this.salerName = salerName;
	}

	public String getSalerAddressPhone() {
		return salerAddressPhone;
	}

	public void setSalerAddressPhone(String salerAddressPhone) {
		this.salerAddressPhone = salerAddressPhone;
	}

	public String getSalerAccount() {
		return salerAccount;
	}

	public void setSalerAccount(String salerAccount) {
		this.salerAccount = salerAccount;
	}

	public String getBuyerAddressPhone() {
		return buyerAddressPhone;
	}

	public void setBuyerAddressPhone(String buyerAddressPhone) {
		this.buyerAddressPhone = buyerAddressPhone;
	}

	public String getBuyerAccount() {
		return buyerAccount;
	}

	public void setBuyerAccount(String buyerAccount) {
		this.buyerAccount = buyerAccount;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(String isCancelled) {
		this.isCancelled = isCancelled;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getCarrierTaxName() {
		return carrierTaxName;
	}

	public void setCarrierTaxName(String carrierTaxName) {
		this.carrierTaxName = carrierTaxName;
	}

	public String getCarrierTaxNum() {
		return carrierTaxNum;
	}

	public void setCarrierTaxNum(String carrierTaxNum) {
		this.carrierTaxNum = carrierTaxNum;
	}

	public String getAcceptTaxName() {
		return acceptTaxName;
	}

	public void setAcceptTaxName(String acceptTaxName) {
		this.acceptTaxName = acceptTaxName;
	}

	public String getAcceptTaxNum() {
		return acceptTaxNum;
	}

	public void setAcceptTaxNum(String acceptTaxNum) {
		this.acceptTaxNum = acceptTaxNum;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverTaxNum() {
		return receiverTaxNum;
	}

	public void setReceiverTaxNum(String receiverTaxNum) {
		this.receiverTaxNum = receiverTaxNum;
	}

	public String getShipperName() {
		return shipperName;
	}

	public void setShipperName(String shipperName) {
		this.shipperName = shipperName;
	}

	public String getShipperTaxNum() {
		return shipperTaxNum;
	}

	public void setShipperTaxNum(String shipperTaxNum) {
		this.shipperTaxNum = shipperTaxNum;
	}

	public String getWayInfo() {
		return wayInfo;
	}

	public void setWayInfo(String wayInfo) {
		this.wayInfo = wayInfo;
	}

	public String getTransportInfo() {
		return transportInfo;
	}

	public void setTransportInfo(String transportInfo) {
		this.transportInfo = transportInfo;
	}

	public String getVehicleNum() {
		return vehicleNum;
	}

	public void setVehicleNum(String vehicleNum) {
		this.vehicleNum = vehicleNum;
	}

	public String getVehicleTonnage() {
		return vehicleTonnage;
	}

	public void setVehicleTonnage(String vehicleTonnage) {
		this.vehicleTonnage = vehicleTonnage;
	}

	public String getBuyerIDNum() {
		return buyerIDNum;
	}

	public void setBuyerIDNum(String buyerIDNum) {
		this.buyerIDNum = buyerIDNum;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getFactoryModel() {
		return factoryModel;
	}

	public void setFactoryModel(String factoryModel) {
		this.factoryModel = factoryModel;
	}

	public String getProductPlace() {
		return productPlace;
	}

	public void setProductPlace(String productPlace) {
		this.productPlace = productPlace;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getCertificateImport() {
		return certificateImport;
	}

	public void setCertificateImport(String certificateImport) {
		this.certificateImport = certificateImport;
	}

	public String getInspectionNum() {
		return inspectionNum;
	}

	public void setInspectionNum(String inspectionNum) {
		this.inspectionNum = inspectionNum;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public String getTaxBureauCode() {
		return taxBureauCode;
	}

	public void setTaxBureauCode(String taxBureauCode) {
		this.taxBureauCode = taxBureauCode;
	}

	public String getTaxBureauName() {
		return taxBureauName;
	}

	public void setTaxBureauName(String taxBureauName) {
		this.taxBureauName = taxBureauName;
	}

	public String getTaxRecords() {
		return taxRecords;
	}

	public void setTaxRecords(String taxRecords) {
		this.taxRecords = taxRecords;
	}

	public String getTonnage() {
		return tonnage;
	}

	public void setTonnage(String tonnage) {
		this.tonnage = tonnage;
	}

	public String getLimitPeople() {
		return limitPeople;
	}

	public void setLimitPeople(String limitPeople) {
		this.limitPeople = limitPeople;
	}

	public String getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}

	public String getTxfbz() {
		return txfbz;
	}

	public void setTxfbz(String txfbz) {
		this.txfbz = txfbz;
	}

	public List<InvoiceDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<InvoiceDetail> detailList) {
		this.detailList = detailList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 4839290190539522047L;

    /**
     * 查验结果
     */
    private String resultCode;

    private String resultTip;

    private String checkCount;

    private String invoiceType;

    private String buyerTaxNo;

    private String buyerName;

    private String invoiceCode;

    private String invoiceNo;

    private String totalAmount;

    private String invoiceAmount;

    private String taxAmount;

    private String machineNo;

    private String salerTaxNo;

    private String salerName;

    private String salerAddressPhone;

    private String salerAccount;

    private String buyerAddressPhone;

    private String buyerAccount;

    private String invoiceDate;

    private String isCancelled;

    private String remark;

    private String checkCode;

    private String carrierTaxName;

    private String carrierTaxNum;

    private String acceptTaxName;

    private String acceptTaxNum;

    private String receiverName;

    private String receiverTaxNum;

    private String shipperName;

    private String shipperTaxNum;

    private String wayInfo;

    private String transportInfo;

    private String vehicleNum;

    private String vehicleTonnage;

    private String buyerIDNum;

    private String vehicleType;

    private String factoryModel;

    private String productPlace;

    private String certificate;

    private String certificateImport;

    private String inspectionNum;

    private String engineNo;

    private String vehicleNo;

    private String taxBureauCode;

    private String taxBureauName;

    private String taxRecords;

    private String tonnage;

    private String limitPeople;

    private String taxRate;

    private String txfbz;

    private List<InvoiceDetail> detailList;
}
