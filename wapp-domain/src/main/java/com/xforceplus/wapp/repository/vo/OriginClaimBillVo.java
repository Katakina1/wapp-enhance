package com.xforceplus.wapp.repository.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 原始索赔单数据
 * @date : 2022/09/08 9:28
 **/
@Data
public class OriginClaimBillVo {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id 关联t_xf_bill_job主键
     */
    private Integer jobId;

    /**
     * 批次号  （导入文件名)
     */
    private String jobName;

    /**
     * 扣款日期
     */
    private String deductionDate;

    /**
     * 扣款日期（Month）
     */
    private String deductionMonth;

    /**
     * 扣款日期（Month Index)
     */
    private String deductionMonthIndex;

    /**
     * 扣款公司
     */
    private String deductionCompany;

    /**
     * 供应商号
     */
    private String vendorNo;

    /**
     * 类型
     */
    private String type;

    /**
     * 备注
     */
    private String remark;

    /**
     * 索赔号/换货号
     */
    @TableField("exchange_no")
    private String exchangeNo;

    /**
     * 索赔号
     */
    private String claimNo;

    /**
     * 定案日期
     */
    private String decisionDate;

    /**
     * 成本金额
     */
    private String costAmount;

    /**
     * 所扣发票
     */
    private String invoiceReference;

    /**
     * 税率
     */
    private String taxRate;

    /**
     * 含税金额
     */
    private String amountWithTax;

    /**
     * 店铺类型（Hyper或Sams）
     */
    private String storeType;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    private Long id;

    /**
     * 数据校验状态；0:正常;1:异常
     */
    private Integer checkStatus;

    /**
     * 数据校验异常信息
     */
    private String checkRemark;
}
