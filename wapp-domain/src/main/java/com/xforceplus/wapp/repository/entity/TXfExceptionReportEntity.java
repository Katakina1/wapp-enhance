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
    * 例外报告
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_exception_report")
public class TXfExceptionReportEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 例外CODE
     */
    @TableField("code")
    private String code;

    /**
     * 例外说明
     */
    @TableField("description")
    private String description;

    /**
     * 供应商名称
     */
    @TableField("seller_name")
    private String sellerName;

    /**
     * 供应商号
     */
    @TableField("seller_no")
    private String sellerNo;

    /**
     * 机构名称，jv的名称，购方名称
     */
    @TableField("purchaser_name")
    private String purchaserName;

    /**
     * jvcode,机构编码，购方编码
     */
    @TableField("purchaser_no")
    private String purchaserNo;

    /**
     * 税率，整数
     */
    @TableField("tax_rate")
    private String taxRate;

    /**
     * 税码
     */
    @TableField("tax_code")
    private String taxCode;

    /**
     * 单据号：索赔单号，协议单号，EPD单号
     */
    @TableField("bill_no")
    private String billNo;

    /**
     * 协议类型编码
     */
    @TableField("agreement_type_code")
    private String agreementTypeCode;

    /**
     * 扣款日期
     */
    @TableField("deduct_date")
    private Date deductDate;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 状态 1 正常，2已处理
     */
    @TableField("status")
    private Integer status;

    /**
     * 文档类型
     */
    @TableField("document_type")
    private String documentType;

    /**
     * 协议供应商6D
     */
    @TableField("agreement_memo")
    private String agreementMemo;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 更新时间
     */
    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 例外报告类型:1 索赔单，2 协议单，3 EPD
     */
    @TableField("type")
    private Integer type;

    /**
     * 定案日期、入账日期
     */
    @TableField("verdict_date")
    private Date verdictDate;

    /**
     * 含税金额
     */
    @TableField("amount_with_tax")
    private BigDecimal amountWithTax;

    /**
     * 不含税金额
     */
    @TableField("amount_without_tax")
    private BigDecimal amountWithoutTax;

    /**
     * 单据ID
     */
    @TableField("bill_id")
    private Long billId;


    public static final String ID = "id";

    public static final String CODE = "code";

    public static final String DESCRIPTION = "description";

    public static final String SELLER_NAME = "seller_name";

    public static final String SELLER_NO = "seller_no";

    public static final String PURCHASER_NAME = "purchaser_name";

    public static final String PURCHASER_NO = "purchaser_no";

    public static final String TAX_RATE = "tax_rate";

    public static final String TAX_CODE = "tax_code";

    public static final String BILL_NO = "bill_no";

    public static final String AGREEMENT_TYPE_CODE = "agreement_type_code";

    public static final String DEDUCT_DATE = "deduct_date";

    public static final String BATCH_NO = "batch_no";

    public static final String STATUS = "status";

    public static final String DOCUMENT_TYPE = "document_type";

    public static final String AGREEMENT_MEMO = "agreement_memo";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPDATE_USER = "update_user";

    public static final String TYPE = "type";

    public static final String VERDICT_DATE = "verdict_date";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String BILL_ID = "bill_id";

}
