package com.xforceplus.wapp.modules.job.pojo;

/**
 * @ClassName:ReturnStateInfo.java
 * @Description:
 * @Description 外层报文
 * @Package com.xforceplus.wapp.modules.job.pojo
 * @author fth
 * @date 2018年04月13日 上午9:56:32
 * @version 1.0
 */
public class ReturnStateInfo extends BasePojo{

	private static final long serialVersionUID = -1343008513507845953L;
	/**
	 * 结果代码
	 */
	private String returnCode;
	/**
	 * 结果描述
	 */
	private String returnMessage;

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	@Override
	public String toString() {
		return "ReturnStateInfo [returnCode=" + returnCode + ", returnMessage=" + returnMessage + "]";
	}
}
