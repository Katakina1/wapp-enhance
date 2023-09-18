package com.xforceplus.wapp.modules.exceptionreport.event;

import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 新增例外报告事件
 *
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-16 20:24
 **/
@Setter
@Getter
@EqualsAndHashCode
public class NewExceptionReportEvent {

	/**
	 * 1、协议单、EPD单、索赔单 实例对象
	 */
	private TXfBillDeductEntity deduct;

	/**
	 * 1、例外报告类型
	 */
	private ExceptionReportTypeEnum type;

	/**
	 * 1、例外报告代码
	 */
	private ExceptionReportCodeEnum reportCode;

	/**
	 * 1、税差
	 */
	private BigDecimal taxBalance;
	
	/**
	 * 1、例外报告原因
	 */
	private String message;

}
