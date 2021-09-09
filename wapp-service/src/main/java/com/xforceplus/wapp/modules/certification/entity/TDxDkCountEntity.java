package com.xforceplus.wapp.modules.certification.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 
 * ****************************************************************************
 * 抵扣统计表实体
 *
 * @author(作者)：xuyongyun	
 * @date(创建日期)：2019年4月24日
 ******************************************************************************
 */
@Setter
@Getter
public class TDxDkCountEntity implements Serializable {
    
	private static final long serialVersionUID = 7667687516272734767L;

	/**
    * 税号
    */
    private String taxno;

    /**
    * 名称
    */
    private String taxname;

    /**
    * 所属期
    */
    private String skssq;

    /**
    * 是否申请(0--未申请 1--申请中 2--申请成功 3--申请失败)
    */
    private String applyStatus;

    /**
    * 申请结果(1-申请成功 2-已申请确认 3-已确认统计4-申请确认月份不符5-税号不存在6-未申请统计7-统计结果不符8-其他异常)
    */
    private String applyMsg;

    /**
    * 是否签名(0--未申请 1--申请中 2--申请成功 3--申请失败)
    */
    private String signStatus;

    /**
    * 统计时间
    */
    private String createDate;

    /**
    * 统计状态
    */
    private String tjStatus;

    /**
    * 确认状态
    */
    private String qsStatus;

    /**
    * 是否当前所属期
    */
    private String sfdqskssq;
    /**
     * 税局统计时间
     */
    private String tjDate;

    /**
     * 申请类型0统计，1撤销统计
     */
    private String applyType;
    /**
     * 撤销统计状态
     */
    private String cxtjStatus;
    /**
     * 撤销确认状态
     */
    private String cxqsStatus;
    /**
     * 集团编码
     */
    private String company;

    private String dksehj;

    private String dkPassword;

    private String dkWhetherPassword;

}
