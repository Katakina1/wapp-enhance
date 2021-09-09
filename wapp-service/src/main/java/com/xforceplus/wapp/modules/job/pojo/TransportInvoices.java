package com.xforceplus.wapp.modules.job.pojo;

/**
 *货运票实体 
 */
public class TransportInvoices extends BasePojo{
  private static final long serialVersionUID = -5929099447307448581L;
	/**************************************** field *****************************************/

	/**
	 * 主键id
	 */
  private Long id;
	/**
	 * 代码+号码
	 */
  private String uuid;
	/**
	 * 承运人
	 */
  private String carrierTaxName;
  /**
	 * 承运人纳税人识别号
	 */
  private String carrierTaxNum;
  /**
	 * 受票方名称
	 */
  private String acceptTaxName;
  /**
	 * 受票方纳税人识别号
	 */
  private String acceptTaxNum;
  /**
	 * 起运地 经由地 到达地
	 */
  private String wayInfo;
  /**
	 * 运输货物信息
	 */
  private String transportInfo;
  /**
	 * 税率
	 */
  private String taxRate;
  /**
	 * 车中车号
	 */
  private String vehicleType;
  /**
	 * 车辆吨位
	 */
  private String vehicleTonnage;
  /**
	 * 主管税务机关代码
	 */
  private String taxBureauCode;
  /**
	 * 主管税务机关名称
	 */
  private String taxBureauName;
  /**
	 * 是否已勾选
	 */
  private String checkStatus;
  /**
	 * 收货人名称 
	 */
  private String receiverName;
  /**
	 * 收货人纳税人识别号
	 */
  private String receiverTaxNum;
  /**
	 * 发货人名称
	 */
  private String shipperName;
  /**
	 * 发货人纳税识别号 
	 */
  private String shipperTaxNum;
  /**
	 * 创建时间
	 */
  private String createDate;
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
	this.uuid = uuid;
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


public String getTaxRate() {
	return taxRate;
}
public void setTaxRate(String taxRate) {
	this.taxRate = taxRate;
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
public String getCheckStatus() {
	return checkStatus;
}
public void setCheckStatus(String checkStatus) {
	this.checkStatus = checkStatus;
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
public String getCreateDate() {
	return createDate;
}
public void setCreateDate(String createDate) {
	this.createDate = createDate;
}
 


}