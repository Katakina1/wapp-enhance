package com.xforceplus.wapp.modules.certification.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 导入页面实体
 * @author Colin.hu
 * @date 4/19/2018
 */
@Getter @Setter @ToString
public class ImportCertificationEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = -2308842182785334065L;

    private Long id;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	/**
     * 不可认证提示
     */
    private String noAuthTip;

    public String getNoAuthTip() {
		return noAuthTip;
	}
	public void setNoAuthTip(String noAuthTip) {
		this.noAuthTip = noAuthTip;
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getInvoiceStatus() {
		return invoiceStatus;
	}
	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}
	public String getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}
	public String getAuthStatus() {
		return authStatus;
	}
	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}
	public String getRzhYesorno() {
		return rzhYesorno;
	}
	public void setRzhYesorno(String rzhYesorno) {
		this.rzhYesorno = rzhYesorno;
	}
	public Boolean getCompareToResult() {
		return compareToResult;
	}
	public void setCompareToResult(Boolean compareToResult) {
		this.compareToResult = compareToResult;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	public String getInvoiceTypeName() {
		return invoiceTypeName;
	}
	public void setInvoiceTypeName(String invoiceTypeName) {
		this.invoiceTypeName = invoiceTypeName;
	}
	public String getRecordFlag() {
		return recordFlag;
	}
	public void setRecordFlag(String recordFlag) {
		this.recordFlag = recordFlag;
	}
	public int getIndexNo() {
		return indexNo;
	}
	public void setIndexNo(int indexNo) {
		this.indexNo = indexNo;
	}
	public String getCurrentTaxPeriod() {
		return currentTaxPeriod;
	}
	public void setCurrentTaxPeriod(String currentTaxPeriod) {
		this.currentTaxPeriod = currentTaxPeriod;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
     * 发票代码
     */
    @NotBlank
    @Length(max = 12)
    private String invoiceCode;

    /**
     * 发票号码
     */
    @NotBlank
    @Length(max = 8)
    private String invoiceNo;

    /**
     * 开票日期
     */
    @NotBlank
    private String invoiceDate;

    /**
     * 金额
     */
    @NotBlank
    private String amount;

    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 发票状态
     */
    private String invoiceStatus;

    /**
     * 税额
     */
    private String taxAmount;

    /**
     * 认证处理状态 ( 4-认证成功 5-认证失败)
     */
    private String authStatus;

    /**
     * 是否认证  0-未认证 1-已认证
     */
    private String rzhYesorno;

    /**
     * 开票日期与认证归属期的比较结果
     */
    private Boolean compareToResult;

    /**
     * 是否有效
     */
    private String valid;

    /**
     * 发票类型名称
     */
    private String invoiceTypeName;

    /**
     * 是否存在抵账（0：不存在 1存在 2 存在但无税号权限）
     */
    private String recordFlag;

    /**
     * excel导入的序号
     */
    private int indexNo;

    /**
     * 后续添加的税款所属期
     * 2018-07-09
     */
    private String currentTaxPeriod;
    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
