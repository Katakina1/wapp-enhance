package com.xforceplus.wapp.modules.job.entity;

import java.io.Serializable;
import java.util.Date;



import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 接口日志表
 * @author panyan
 *
 */

@Getter @Setter
public class TDxDkCountDetail implements Serializable{
	
	private static final long serialVersionUID = 1L;
	

	private Long id;

	/**
	 * 税号
	 */
    private String taxno;
    /**
     * 统计月份
     */
    private String tjMonth;
    /**
     * 统计时间
     */
    private String tjDate;
    /**
     * 发票类型
     */
    private String invoiceType;
    /**
     * 抵扣发票数量
     */
    private String dkInvoiceCount;
    /**
     * 抵扣发票总额
     */
    private String dkAmountCount;
    /**
     * 抵扣总有效税额
     */
    private String dkTaxAmountCount;
    /**
     * 不抵扣发票数量
     */
    private String bdkInvoiceCount;
    /**
     * 不抵扣发票总额
     */
    private String bdkAmountCount;
    /**
     * 不抵扣总有效税额
     */
    private String bdkTaxAmountCount;
   

    
}