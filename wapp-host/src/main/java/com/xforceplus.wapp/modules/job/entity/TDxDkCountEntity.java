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
public class TDxDkCountEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	

	private Long id;

	/**
	 * 税号
	 */
    private String taxno;
    /**
     * 纳税人名称
     */
    private String taxname;
    /**
     * 税款所属期
     */
    private String skssq;
    /**
     * 申请状态
     */
    private String applyStatus;
    /**
     * 申请结果
     */
    private String applyMsg;
    /**
     * 是否签名 ？
     */
    private String signStatus;
    /**
     * 统计状态
     */
    private String tjStatus;
    /**
     * 确认状态
     */
    private String qsStatus;
    /**
     * 是否当前税款所属期
     */
    private String sfdqskssq;
    /**
     * 申请统计类型
     */
    private String applyType;
    /**
     *统计时间 
     */
    private String tjDate;
    
    private String createDate;
    
    private String company;
    
    private String ywmm;
    
    
}