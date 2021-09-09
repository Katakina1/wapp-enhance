package com.xforceplus.wapp.modules.job.pojo;

import java.util.List;

public class TransportInvoiceInfos extends BasePojo{

	private static final long serialVersionUID = 9140037500021701297L;
	private String invoiceType;
	private String invoiceCode;
	private String invoiceNo;
	private String currentTaxPeriod;
	private String legalizeEndDate;
	private String legalizeInvoiceDateBegin;
	private String legalizeInvoiceDateEnd;
	private String  invoiceDate;
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
    private String invoiceAmount;
	private String taxRate;
	private String taxAmount;
	private String totalAmount;
	private String vehicleType;
	private String vehicleTonnage;
	private String taxBureauCode;
	private String taxBureauName;
	private String remark;
	private String invoiceStatus;
	private String checkStatus;
	private String legalizeState;
	private String legalizeDate;
	private String taxPeriod;
    private String legalizeType;
	private List<TransportDetailInfos>detailList;
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
	public String getCurrentTaxPeriod() {
		return currentTaxPeriod;
	}
	public void setCurrentTaxPeriod(String currentTaxPeriod) {
		this.currentTaxPeriod = currentTaxPeriod;
	}
	public String getLegalizeEndDate() {
		return legalizeEndDate;
	}
	public void setLegalizeEndDate(String legalizeEndDate) {
		this.legalizeEndDate = legalizeEndDate;
	}
	public String getLegalizeInvoiceDateBegin() {
		return legalizeInvoiceDateBegin;
	}
	public void setLegalizeInvoiceDateBegin(String legalizeInvoiceDateBegin) {
		this.legalizeInvoiceDateBegin = legalizeInvoiceDateBegin;
	}
	public String getLegalizeInvoiceDateEnd() {
		return legalizeInvoiceDateEnd;
	}
	public void setLegalizeInvoiceDateEnd(String legalizeInvoiceDateEnd) {
		this.legalizeInvoiceDateEnd = legalizeInvoiceDateEnd;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
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
	public String getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public String getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
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
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getVehicleTonnage() {
		return vehicleTonnage;
	}
	public void setVehicleTonnage(String vehicleTonnage) {
		this.vehicleTonnage = vehicleTonnage;
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
	public String getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}
	public String getLegalizeState() {
		return legalizeState;
	}
	public void setLegalizeState(String legalizeState) {
		this.legalizeState = legalizeState;
	}
	public String getLegalizeDate() {
		return legalizeDate;
	}
	public void setLegalizeDate(String legalizeDate) {
		this.legalizeDate = legalizeDate;
	}
	public String getTaxPeriod() {
		return taxPeriod;
	}
	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}
	public String getLegalizeType() {
		return legalizeType;
	}
	public void setLegalizeType(String legalizeType) {
		this.legalizeType = legalizeType;
	}
	
	

	public List<TransportDetailInfos> getDetailList() {
		return detailList;
	}
	public void setDetailList(List<TransportDetailInfos> detailList) {
		this.detailList = detailList;
	}
	@Override
	public String toString() {
		return "TransportInvoiceInfos [invoiceType=" + invoiceType
				+ ", invoiceCode=" + invoiceCode + ", invoiceNo=" + invoiceNo
				+ ", currentTaxPeriod=" + currentTaxPeriod
				+ ", legalizeEndDate=" + legalizeEndDate
				+ ", legalizeInvoiceDateBegin=" + legalizeInvoiceDateBegin
				+ ", legalizeInvoiceDateEnd=" + legalizeInvoiceDateEnd
				+ ", invoiceDate=" + invoiceDate + ", carrierTaxName="
				+ carrierTaxName + ", carrierTaxNum=" + carrierTaxNum
				+ ", acceptTaxName=" + acceptTaxName + ", acceptTaxNum="
				+ acceptTaxNum + ", receiverName=" + receiverName
				+ ", receiverTaxNum=" + receiverTaxNum + ", shipperName="
				+ shipperName + ", shipperTaxNum=" + shipperTaxNum
				+ ", wayInfo=" + wayInfo + ", transportInfo=" + transportInfo
				+ ", invoiceAmount=" + invoiceAmount + ", taxRate=" + taxRate
				+ ", taxAmount=" + taxAmount + ", totalAmount=" + totalAmount
				+ ", vehicleType=" + vehicleType + ", vehicleTonnage="
				+ vehicleTonnage + ", taxBureauCode=" + taxBureauCode
				+ ", taxBureauName=" + taxBureauName + ", remark=" + remark
				+ ", invoiceStatus=" + invoiceStatus + ", checkStatus="
				+ checkStatus + ", legalizeState=" + legalizeState
				+ ", legalizeDate=" + legalizeDate + ", taxPeriod=" + taxPeriod
				+ ", legalizeType=" + legalizeType + ", detailList="
				+ detailList + "]";
	}


	
}
