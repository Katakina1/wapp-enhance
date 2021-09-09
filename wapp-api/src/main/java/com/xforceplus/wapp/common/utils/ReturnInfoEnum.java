package com.xforceplus.wapp.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 查验返回信息枚举
 * @author Colin.hu
 * @date 4/18/2018
 */
@Getter
public enum  ReturnInfoEnum {

    /**
     * 查验出错
     */
    SYS_ERROR("9999", "查验失败"),

    /**
     * 查验成功
     */
    CHECK_SUCCESS("0001", "查验成功发票一致");

    private ReturnInfoEnum(String resultCode, String resultTip) {
		this.resultCode = resultCode;
		this.resultTip = resultTip;
	}

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

	private String resultCode;

    private String resultTip;
}
