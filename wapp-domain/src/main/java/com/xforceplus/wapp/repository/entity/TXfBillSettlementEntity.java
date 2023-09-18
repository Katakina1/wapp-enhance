package com.xforceplus.wapp.repository.entity;

import java.math.BigDecimal;
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
 * 1、业务单和结算单关联关系
 * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-12-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "t_xf_bill_settlement")
public class TXfBillSettlementEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;
	
	@TableField("bill_id")
	private Long billId;
	/**
	 * 业务单据编号
	 */
	@TableField("business_no")
	private String businessNo;

	/**
	 * 1、业务单据类型;1:索赔;2:协议;3:EPD
	 */
	@TableField("business_type")
	private Integer businessType;

	/**
	 * 1、结算单号
	 */
	@TableField("settlement_no")
	private String settlementNo;

	/**
	 * 1、状态 0 正常，1作废
	 */
	@TableField("status")
	private Integer status;

	/**
	 * 1、业务单状态
	 */
	@TableField("biil_status")
	private Integer biilStatus;

	/**
	 * 1、结算单状态
	 */
	@TableField("settlment_status")
	private Integer settlmentStatus;

	/**
	 * 1、销方编号
	 */
	@TableField("seller_no")
	private String sellerNo;

	/**
	 * 1、销方名称
	 */
	@TableField("seller_name")
	private String sellerName;

	/**
	 * 1、购方编号
	 */
	@TableField("purchaser_no")
	private String purchaserNo;

	/**
	 * 1、购方名称
	 */
	@TableField("purchaser_name")
	private String purchaserName;

	/**
	 * 1、业务单不含税
	 */
	@TableField("biil_amount_without_tax")
	private BigDecimal biilAmountWithoutTax;

	/**
	 * 1、业务单税金
	 */
	@TableField("biil_tax_amount")
	private BigDecimal biilTaxAmount;

	/**
	 * 1、结算单不含税
	 */
	@TableField("settlment_amount_without_tax")
	private BigDecimal settlmentAmountWithoutTax;

	/**
	 * 1、结算单税金
	 */
	@TableField("settlment_tax_amount")
	private BigDecimal settlmentTaxAmount;

	@TableField(value = "update_time", update = "getdate()")
	private Date updateTime;

	@TableField("create_time")
	private Date createTime;

	public static final String BILL_ID = "bill_id";
	
	public static final String BUSINESS_NO = "business_no";

	public static final String BUSINESS_TYPE = "business_type";

	public static final String SETTLEMENT_NO = "settlement_no";

	public static final String STATUS = "status";

	public static final String BIIL_STATUS = "biil_status";

	public static final String SETTLMENT_STATUS = "settlment_status";

	public static final String SELLER_NO = "seller_no";

	public static final String SELLER_NAME = "seller_name";

	public static final String PURCHASER_NO = "purchaser_no";

	public static final String PURCHASER_NAME = "purchaser_name";

	public static final String BIIL_AMOUNT_WITHOUT_TAX = "biil_amount_without_tax";

	public static final String BIIL_TAX_AMOUNT = "biil_tax_amount";

	public static final String SETTLMENT_AMOUNT_WITHOUT_TAX = "settlment_amount_without_tax";

	public static final String SETTLMENT_TAX_AMOUNT = "settlment_tax_amount";

	public static final String UPDATE_TIME = "update_time";

	public static final String CREATE_TIME = "create_time";

	public static final String ID = "id";

}
