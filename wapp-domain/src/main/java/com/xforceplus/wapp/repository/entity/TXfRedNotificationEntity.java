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
    * 红字信息表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_red_notification")
public class TXfRedNotificationEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 业务主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 开票日期
     */
    @TableField("invoice_date")
    private Date invoiceDate;

    /**
     * 红字信息唯一标识
     */
    @TableField("pid")
    private String pid;

    /**
     * 1 销方 2购方
     */
    @TableField("user_role")
    private Integer userRole;

    /**
     * 申请类型 购方发起:0-已抵扣1-未抵扣 销方发起:2-开票有误
     */
    @TableField("apply_type")
    private Integer applyType;

    /**
     * 申请失败原因
     */
    @TableField("apply_remark")
    private String applyRemark;

    /**
     * 1.未申请 2.申请中 3.已申请 4.撤销待审核
     */
    @TableField("applying_status")
    private Integer applyingStatus;

    /**
     * 审批状态 1. 审核通过,2. 审核不通过,3. 已核销,4. 已撤销
     */
    @TableField("approve_status")
    private Integer approveStatus;

    /**
     * 红字信息表编号
     */
    @TableField("red_notification_no")
    private String redNotificationNo;

    /**
     * 申请原因
     */
    @TableField("apply_reason")
    private String applyReason;

    /**
     * 发票类型 （01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票）
     */
    @TableField("invoice_type")
    private String invoiceType;

    /**
     * 发票号码
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 发票代码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 原发票类型
     */
    @TableField("origin_invoice_type")
    private String originInvoiceType;

    /**
     * 购方税号
     */
    @TableField("purchaser_tax_no")
    private String purchaserTaxNo;

    /**
     * 购方名称
     */
    @TableField("purchaser_name")
    private String purchaserName;

    /**
     * 销方税号
     */
    @TableField("seller_tax_no")
    private String sellerTaxNo;

    /**
     * 销方名称
     */
    @TableField("seller_name")
    private String sellerName;

    /**
     * 不含税金额
     */
    @TableField("amount_without_tax")
    private Double amountWithoutTax;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private Double taxAmount;

    /**
     * 含税金额
     */
    @TableField("amount_with_tax")
    private Double amountWithTax;

    /**
     * 扣除额
     */
    @TableField("deduction")
    private Double deduction;

    /**
     * 计价方式
     */
    @TableField("price_method")
    private Integer priceMethod;

    /**
     * 单号
     */
    @TableField("bill_no")
    private String billNo;

    /**
     * 红字信息状态0 删除 1 正常
     */
    @TableField("status")
    private Integer status;

    /**
     * 红字信息来源1.索赔单，2协议单，3.EPD
     */
    @TableField("invoice_origin")
    private Integer invoiceOrigin;

    /**
     * 公司编号
     */
    @TableField("company_code")
    private String companyCode;

    /**
     * 设备唯一码
     */
    @TableField("device_un")
    private String deviceUn;

    /**
     * 终端唯一码
     */
    @TableField("terminal_un")
    private String terminalUn;

    /**
     * 特殊发票标记 0-默认  1-通行费   2-成品油
     */
    @TableField("special_invoice_flag")
    private Integer specialInvoiceFlag;

    /**
     * 扣款时间
     */
    @TableField("payment_time")
    private Date paymentTime;

    /**
     * 客户编号
     */
    @TableField("customer_no")
    private String customerNo;

    /**
     * 创建日期
     */
    @TableField("create_date")
    private Date createDate;

    /**
     * 更新日期
     */
    @TableField("update_date")
    private Date updateDate;

    /**
     * 用户账号
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    @TableField("remark")
    private String remark;

    @TableField("terminal_type")
    private Integer terminalType;

    @TableField("user_id")
    private Long userId;


    public static final String ID = "id";

    public static final String INVOICE_DATE = "invoice_date";

    public static final String PID = "pid";

    public static final String USER_ROLE = "user_role";

    public static final String APPLY_TYPE = "apply_type";

    public static final String APPLY_REMARK = "apply_remark";

    public static final String APPLYING_STATUS = "applying_status";

    public static final String APPROVE_STATUS = "approve_status";

    public static final String RED_NOTIFICATION_NO = "red_notification_no";

    public static final String APPLY_REASON = "apply_reason";

    public static final String INVOICE_TYPE = "invoice_type";

    public static final String INVOICE_NO = "invoice_no";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String ORIGIN_INVOICE_TYPE = "origin_invoice_type";

    public static final String PURCHASER_TAX_NO = "purchaser_tax_no";

    public static final String PURCHASER_NAME = "purchaser_name";

    public static final String SELLER_TAX_NO = "seller_tax_no";

    public static final String SELLER_NAME = "seller_name";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String DEDUCTION = "deduction";

    public static final String PRICE_METHOD = "price_method";

    public static final String BILL_NO = "bill_no";

    public static final String STATUS = "status";

    public static final String INVOICE_ORIGIN = "invoice_origin";

    public static final String COMPANY_CODE = "company_code";

    public static final String DEVICE_UN = "device_un";

    public static final String TERMINAL_UN = "terminal_un";

    public static final String SPECIAL_INVOICE_FLAG = "special_invoice_flag";

    public static final String PAYMENT_TIME = "payment_time";

    public static final String CUSTOMER_NO = "customer_no";

    public static final String CREATE_DATE = "create_date";

    public static final String UPDATE_DATE = "update_date";

    public static final String USER_ACCOUNT = "user_account";

    public static final String USER_NAME = "user_name";

    public static final String REMARK = "remark";

    public static final String TERMINAL_TYPE = "terminal_type";

    public static final String USER_ID = "user_id";

}
