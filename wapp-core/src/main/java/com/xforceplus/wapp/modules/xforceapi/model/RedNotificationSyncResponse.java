package com.xforceplus.wapp.modules.xforceapi.model;

import java.io.Serializable;

/**
 * 红字信息表下载响应参数
 * 
 * @author just
 *
 */
public class RedNotificationSyncResponse extends XforceApiResponse implements Serializable{

	private static final long serialVersionUID = -8374520810555762032L;
	
	private Result result;

	public static class Result {
		//返回流水号，结果获取时同步
		private String serialNo;

		public String getSerialNo() {
			return serialNo;
		}

		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}
		
		
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}
	
	
}
