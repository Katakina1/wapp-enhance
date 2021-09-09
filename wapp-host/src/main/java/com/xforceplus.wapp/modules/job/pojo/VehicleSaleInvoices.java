package com.xforceplus.wapp.modules.job.pojo;

/**
 *机动车实体 
 */
public class VehicleSaleInvoices extends BasePojo{
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
	 * 购方身份证号码 组织 机构代码
	 */
  private String buyerIdNum;
  /**
	 * 车辆类型
	 */
  private String vehicleType;
  /**
	 * 厂牌型号
	 */
  private String factoryModel;
  /**
	 * 产地
	 */
  private String productPlace;
  /**
	 * 合格证书
	 */
  private String certificate;
  /**
	 * 进口证明书号
	 */
  private String certificateImprot;
  /**
	 * 商检单号
	 */
  private String inspectionNum;
  /**
	 * 发动机号
	 */
  private String engineNo;
  /**
	 * 车架号码 车辆识别码
	 */
  private String vehicleNo;
  /**
	 * 电话
	 */
  private String phone;
  /**
	 * 开户银行
	 */
  private String buyerBank;
  /**
	 * 税率
	 */
  private String taxRate;
  /**
	 * 主管税务机关名称 
	 */
  private String taxBureauName;
  /**
	 * 主管税务机关代码
	 */
  private String taxBureauCode;
  /**
	 * 完税凭证号码
	 */
  private String taxRecords;
  /**
	 * 限乘人数 
	 */
  private String limitPeople;
  /**
	 * 是否已勾选 0-未勾选 1-已勾选
	 */
  private String checkStatus;
  /**
	 * 吨位
	 */
  private String tonnage;

  public Long getId()
  {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getBuyerIdNum() {
    return this.buyerIdNum;
  }

  public void setBuyerIdNum(String buyerIdNum) {
    this.buyerIdNum = buyerIdNum;
  }

  public String getVehicleType() {
    return this.vehicleType;
  }

  public void setVehicleType(String vehicleType) {
    this.vehicleType = vehicleType;
  }

  public String getFactoryModel() {
    return this.factoryModel;
  }

  public void setFactoryModel(String factoryModel) {
    this.factoryModel = factoryModel;
  }

  public String getProductPlace() {
    return this.productPlace;
  }

  public void setProductPlace(String productPlace) {
    this.productPlace = productPlace;
  }

  public String getCertificate() {
    return this.certificate;
  }

  public void setCertificate(String certificate) {
    this.certificate = certificate;
  }

  public String getCertificateImprot() {
    return this.certificateImprot;
  }

  public void setCertificateImprot(String certificateImprot) {
    this.certificateImprot = certificateImprot;
  }

  public String getInspectionNum() {
    return this.inspectionNum;
  }

  public void setInspectionNum(String inspectionNum) {
    this.inspectionNum = inspectionNum;
  }

  public String getEngineNo() {
    return this.engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  public String getVehicleNo() {
    return this.vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getPhone() {
    return this.phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getBuyerBank() {
    return this.buyerBank;
  }

  public void setBuyerBank(String buyerBank) {
    this.buyerBank = buyerBank;
  }

  public String getTaxRate() {
    return this.taxRate;
  }

  public void setTaxRate(String taxRate) {
    this.taxRate = taxRate;
  }

  public String getTaxBureauName() {
    return this.taxBureauName;
  }

  public void setTaxBureauName(String taxBureauName) {
    this.taxBureauName = taxBureauName;
  }

  public String getTaxBureauCode() {
    return this.taxBureauCode;
  }

  public void setTaxBureauCode(String taxBureauCode) {
    this.taxBureauCode = taxBureauCode;
  }

  public String getLimitPeople()
  {
    return this.limitPeople;
  }

  public void setLimitPeople(String limitPeople) {
    this.limitPeople = limitPeople;
  }

  public String getCheckStatus() {
    return this.checkStatus;
  }

  public void setCheckStatus(String checkStatus) {
    this.checkStatus = checkStatus;
  }

  public String getTaxRecords() {
    return this.taxRecords;
  }

  public void setTaxRecords(String taxRecords) {
    this.taxRecords = taxRecords;
  }

  public String getTonnage() {
    return this.tonnage;
  }

  public void setTonnage(String tonnage) {
    this.tonnage = tonnage;
  }
}