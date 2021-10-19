package com.xforceplus.wapp.modules.exceptionreport.dto;

import com.xforceplus.wapp.dto.PageViewRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 20:49
 **/
@Setter
@Getter
public class ExceptionReportRequest extends PageViewRequest {
    /**
     * 扣款日期
     */
    private String startDeductDate;
    /**
     * 扣款日期
     */
    private String endDeductDate;
    /**
     * 供应商号
     */
    private String sellerNo;
    /**
     * 供应商名称
     */
    private String sellerName;
    /**
     * 单据号：索赔单号，协议号，EPD号
     */
    private String billNo;

    private Long userId;


}
