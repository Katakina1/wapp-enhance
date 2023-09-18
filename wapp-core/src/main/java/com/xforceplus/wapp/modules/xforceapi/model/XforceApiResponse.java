package com.xforceplus.wapp.modules.xforceapi.model;

import org.apache.commons.lang3.StringUtils;

/**
 * xforce response统一格式
 * 
 * @author just
 *
 */
public class XforceApiResponse {
	//成功的错误代码
	public static final String SUCCESS_CODE = "TXWR000000";
	// 结果code
	private String code;// ": "TXWR000000",
	// 消息提示
	private String message;// ": "成功",
	// traceId
	private String traceId;// ": null,
	
	public XforceApiResponse() {
	}

	/**
	 * <pre>
	 * 判断请求是否成功
	 * </pre>
	 * @return
	 */
	public boolean isOk() {
		return StringUtils.equalsIgnoreCase(code, SUCCESS_CODE);
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

}
