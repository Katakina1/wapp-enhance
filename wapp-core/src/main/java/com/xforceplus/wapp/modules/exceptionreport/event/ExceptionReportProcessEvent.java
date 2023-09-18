package com.xforceplus.wapp.modules.exceptionreport.event;

import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;

import lombok.Data;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-03-31 11:27
 **/
@Data
public class ExceptionReportProcessEvent {

    /**
     * 业务单号
     */
    private String billNo;
    
    /**
	 * 1、协议单、EPD单、索赔单 实例对象
	 */
	private TXfBillDeductEntity deduct;

    /**
     * 例外报告类型
     */
    private ExceptionReportTypeEnum type;


    /**
     * 例外报告代码
     */
    private ExceptionReportCodeEnum reportCode;

    private Long billId;

}
