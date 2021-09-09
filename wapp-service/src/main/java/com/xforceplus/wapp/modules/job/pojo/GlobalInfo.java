package com.xforceplus.wapp.modules.job.pojo;

/**
 * 
 * @Title GobalInfo.java
 * @Description 外层报文
 * @Package com.xforceplus.wapp.modules.job.pojo
 * @author fth
 * @date 2018年04月13日 上午9:56:32
 * @version 1.0
 */
public class GlobalInfo extends BasePojo{

	private static final long serialVersionUID = 8145333412451825323L;
	/**
	 * JXFP 大象提供
	 */
	private String appId;
	 /**
	  * 接口版本
	  */
	private String version;
	/**
	 * 接口编码
	 */
	private String interfaceCode;
	/**
	 * 企业代码
	 */
	private String enterpriseCode;
	/**
	 * 数据交换流水号
	 */
	private String dataExchangeId;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getInterfaceCode() {
		return interfaceCode;
	}

	public void setInterfaceCode(String interfaceCode) {
		this.interfaceCode = interfaceCode;
	}

	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	public String getDataExchangeId() {
		return dataExchangeId;
	}

	public void setDataExchangeId(String dataExchangeId) {
		this.dataExchangeId = dataExchangeId;
	}

	@Override
	public String toString() {
		return "GlobalInfo [appId=" + appId + ", version=" + version + ", interfaceCode=" + interfaceCode
				+ ", enterpriseCode=" + enterpriseCode + ", dataExchangeId=" + dataExchangeId + "]";
	}

}
