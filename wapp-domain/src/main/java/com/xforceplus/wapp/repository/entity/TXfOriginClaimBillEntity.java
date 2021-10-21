package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 原始索赔单数据
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_origin_claim_bill")
public class TXfOriginClaimBillEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id 关联t_xf_bill_job主键
     */
    @TableField("job_id")
    private Integer jobId;

    /**
     * 扣款日期
     */
    @TableField("deduction_date")
    private String deductionDate;

    /**
     * 扣款日期（Month）
     */
    @TableField("deduction_month")
    private String deductionMonth;

    /**
     * 扣款日期（Month Index)
     */
    @TableField("deduction_month_index")
    private String deductionMonthIndex;

    /**
     * 扣款公司
     */
    @TableField("deduction_company")
    private String deductionCompany;

    /**
     * 供应商号
     */
    @TableField("vendor_no")
    private String vendorNo;

    /**
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 索赔号/换货号
     */
    @TableField("exchange_no")
    private String exchangeNo;

    /**
     * 索赔号
     */
    @TableField("claim_no")
    private String claimNo;

    /**
     * 定案日期
     */
    @TableField("decision_date")
    private String decisionDate;

    /**
     * 成本金额
     */
    @TableField("cost_amount")
    private String costAmount;

    /**
     * 所扣发票
     */
    @TableField("invoice_reference")
    private String invoiceReference;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private String taxRate;

    /**
     * 含税金额
     */
    @TableField("amount_with_tax")
    private String amountWithTax;

    /**
     * 店铺类型（Hyper或Sams）
     */
    @TableField("store_type")
    private String storeType;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    public static final String JOB_ID = "job_id";

    public static final String DEDUCTION_DATE = "deduction_date";

    public static final String DEDUCTION_MONTH = "deduction_month";

    public static final String DEDUCTION_MONTH_INDEX = "deduction_month_index";

    public static final String DEDUCTION_COMPANY = "deduction_company";

    public static final String VENDOR_NO = "vendor_no";

    public static final String TYPE = "type";

    public static final String REMARK = "remark";

    public static final String EXCHANGE_NO = "exchange_no";

    public static final String CLAIM_NO = "claim_no";

    public static final String DECISION_DATE = "decision_date";

    public static final String COST_AMOUNT = "cost_amount";

    public static final String INVOICE_REFERENCE = "invoice_reference";

    public static final String TAX_RATE = "tax_rate";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String STORE_TYPE = "store_type";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String ID = "id";

}
