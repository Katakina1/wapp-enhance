package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 1、海关缴款书
 * </p>
 *
 * @author pengtao@xforceplus.com
 * @since 2023-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "t_dx_customs")
public class TDxCustomsEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@ApiModelProperty("缴款书号码")
	@TableField("customs_no")
	private String customsNo;
	
	@ApiModelProperty("缴款书来源1-缴款书采集、0-底账同步")
	@TableField("source_type")
	private String sourceType;

	@ApiModelProperty("购方税号")
	@TableField("company_tax_no")
	private String companyTaxNo;

	@ApiModelProperty("购方公司")
	@TableField("company_name")
	private String companyName;

	@ApiModelProperty("填发日期")
	@TableField("paper_drew_date")
	private String paperDrewDate;

	@ApiModelProperty("税款金额")
	@TableField("tax_amount")
	private BigDecimal taxAmount;

	@ApiModelProperty("可抵扣税款金额")
	@TableField("effective_tax_amount")
	private BigDecimal effectiveTaxAmount;
	/**
	 * 勾选状态 -1 - 撤销勾选失败 0-撤销勾选中 1-不可勾选  2-未勾选 3-勾选中4-已勾选 5-勾选失败  6-抵扣异常 8-已确认抵扣 9-撤销勾选成功(属地使用)
	 */
	@ApiModelProperty("勾选状态1-勾选/撤销勾选成功 0-勾选/撤销勾选中 -1 -勾选/撤销勾选失败")
	@TableField("is_check")
	private String isCheck;

	@ApiModelProperty("勾选时间")
	@TableField("check_time")
	private Date checkTime;

	@ApiModelProperty("抵扣用途:1-抵扣勾选,2-不抵扣勾选")
	@TableField("check_purpose")
	private String checkPurpose;

	@ApiModelProperty("勾选人ID")
	@TableField("check_user_id")
	private String checkUserId;

	@ApiModelProperty("勾选人")
	@TableField("check_user_name")
	private String checkUserName;

	@ApiModelProperty("认证备注")
	@TableField("auth_remark")
	private String authRemark;

	@ApiModelProperty("管理状态1-正常、0-非正常")
	@TableField("manage_status")
	private String manageStatus;

	@ApiModelProperty("统计状态待申请统计、统计中、统计完成、撤销统计中、撤销统计失败")
	@TableField("count_status")
	private String countStatus;

	@ApiModelProperty("统计时间")
	@TableField("count_time")
	private Date countTime;

	@ApiModelProperty("签名确认状态确认中、已确认、撤销确认中、撤销确认失败")
	@TableField("sign_status")
	private String signStatus;

	@ApiModelProperty("签名确认时间")
	@TableField("sign_time")
	private Date signTime;

	@ApiModelProperty("入账状态未入账、企业所得税税前扣除、企业所得税不扣除、撤销入账")
	@TableField("account_status")
	private String accountStatus;

	@ApiModelProperty("凭证入账时间")
	@TableField("voucher_account_time")
	private Date voucherAccountTime;

	@ApiModelProperty("国税入账时间")
	@TableField("account_time")
	private Date accountTime;

	@ApiModelProperty("科目")
	@TableField("payee_subject")
	private String payeeSubject;

	@ApiModelProperty("所属期")
	@TableField("tax_period")
	private String taxPeriod;

	@ApiModelProperty("凭证号")
	@TableField("voucher_no")
	private String voucherNo;

	@ApiModelProperty("推送bms -1推送失败 为空或0-未推送 1-推送成功")
	@TableField("push_bms_status")
	private String pushBmsStatus;

	@ApiModelProperty("推送bms时间")
	@TableField("push_bms_time")
	private Date pushBmsTime;

	@ApiModelProperty("是否手工认证 0-否 1-是")
	@TableField("confirm_status")
	private String confirmStatus;

	@ApiModelProperty("比对状态 -1-比对失败 0-未比对 1-比对成功")
	@TableField("bill_status")
	private String billStatus;

	@ApiModelProperty("税额差 税额差是主表的税额减去缴款书明细税额合计差额")
	@TableField("tax_amount_difference")
	private BigDecimal taxAmountDifference;

	@ApiModelProperty("海关票异常信息")
	@TableField("abnormal_info")
	private String abnormalInfo;

	@ApiModelProperty("撤销勾选日期")
	@TableField("un_check_time")
	private Date unCheckTime;

	@ApiModelProperty("PO号")
	@TableField("contract_no")
	private String contractNo;

	@ApiModelProperty("报关单编号(从BMS获取返回)")
	@TableField("customs_doc_no")
	private String customsDocNo;

	@TableField(value = "update_time", update = "getdate()")
	private Date updateTime;

	@TableField("create_time")
	private Date createTime;

	public static final String CUSTOMSNO = "customs_no";

	public static final String SOURCETYPE = "source_type";

	public static final String COMPANYTAXNO = "company_tax_no";

	public static final String COMPANYNAME = "compan_name";

	public static final String ISCHECK = "is_check";

	public static final String SIGNSTATUS = "sign_status";

	public static final String ACCOUNTTIME = "account_time";

	public static final String CHECKUSERNAME = "check_user_name";
}
