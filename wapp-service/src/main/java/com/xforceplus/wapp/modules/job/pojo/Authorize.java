package com.xforceplus.wapp.modules.job.pojo;

/**
 * 
 * @Title Authorize.java
 * @Description 授权信息
 * @author X Yang
 * @date 2017年6月7日 上午10:04:11
 */
public class Authorize extends BasePojo{

	private static final long serialVersionUID = -77529363102915241L;

	private String appSecId;
	
	private String appSec;

	public String getAppSecId() {
		return appSecId;
	}

	public void setAppSecId(String appSecId) {
		this.appSecId = appSecId;
	}

	public String getAppSec() {
		return appSec;
	}

	public void setAppSec(String appSec) {
		this.appSec = appSec;
	}

	@Override
	public String toString() {
		return "Authorize [appSecId=" + appSecId + ", appSecKey=" + appSec + "]";
	}
}
