package com.xforceplus.wapp.modules.backfill.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 云识别异步通知结果
 * https://wiki.xforceplus.com/pages/viewpage.action?pageId=5544015
 * @author zhaochao
 * @date 2021-9-15 20:52:43
 *
 */
@Data
public class DiscernResult implements Serializable {
	

	/**
	 * 成功
	 */
	public static final Integer  CODE_SUCCESS = 1;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 任务ID
	 */
	private String taskId;
	/**
	 * 识别状态	
	 * 0-待识别 1-已识别 2-识别失败 3-识别中  4-不需要识别
	 */
	private Integer discernStatus;
	/**
	 * 识别结果类型(新)	
	 * https://wiki.xforceplus.com/pages/viewpage.action?pageId=5542545
	 */
	private String documentType;
	/**
	 * 识别结果类型
	 * 1-结算单

		2-增值税普通发票
		
		3-增值税专用发票-发票联
		
		4-增值税专用发票-抵扣联
		
		5-增值税电子普通发票
		
		6-附件
		
		7-错误文件
		
		8-机动车销售发票
		
		9-增值税普通发票(卷票)
		
		10-增值税电子普通发票(增值税)
		
		0-未定义
	 */
	private Integer discernResultType;
	/**
	 * 集成平台参数
	 */
	private String customerNo;
	
	private String serialNo;
	/**
	 * 保留字
	 * 接入方调用接口时候传入的值，此处会原样返回
	 */
	private String reserved;
	
	/**
	 * 结果详情
	 */
	private String discernResult;
	

}
