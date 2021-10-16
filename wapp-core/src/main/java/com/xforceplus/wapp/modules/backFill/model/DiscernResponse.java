package com.xforceplus.wapp.modules.backFill.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DiscernResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 1-成功
	 *  非1-失败
	 */
	private Integer code;
	
	private String message;
	
	private Integer status;
	
	/**
	 * 云识别返回结果
	 */
	private Result result;
	
	@Data
	public class Result {
		
		/**
		 * 返回识别任务号
		 */
		private String taskId;
	}
	
}
