//package com.xforceplus.wapp.repository.entity;
//
//import java.math.BigDecimal;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.baomidou.mybatisplus.annotation.IdType;
//import java.util.Date;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.xforceplus.wapp.repository.entity.BaseEntity;
//import com.baomidou.mybatisplus.annotation.TableField;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.ToString;
///**
// * <p>
//    * 发票表
//    * </p>
// *
// * @author malong@xforceplus.com
// * @since 2021-10-21
// */
//@Data
//@EqualsAndHashCode(callSuper = true)
//@ToString(callSuper=true)
//@TableName(value="t_xf_invoice")
//public class TXfInvoiceEntity extends BaseEntity {
//
//    private static final long serialVersionUID = 1L;
//
//    /**
//     * 发票代码
//     */
//    @TableField("invoice_code")
//    private String invoiceCode;
//
//    /**
//     * 发票号码
//     */
//    @TableField("invoice_no")
//    private String invoiceNo;
//
//    /**
//     * 开票日期
//     */
//    @TableField("paper_drew_date")
//    private String paperDrewDate;
//
//    /**
//     * 发票类型
//     */
//    @TableField("invoice_type")
//    private String invoiceType;
//
//    /**
//     * 购方名称
//     */
//    @TableField("purchaser_name")
//    private String purchaserName;
//
//    /**
//     * 购方纳税人识别号
//     */
//    @TableField("purchaser_tax_no")
//    private String purchaserTaxNo;
//
//    /**
//     * 购方地址电话
//     */
//    @TableField("purchaser_addr_tel")
//    private String purchaserAddrTel;
//
//    /**
//     * 购方银行名称与账号合并
//     */
//    @TableField("purchaser_bank_name_account")
//    private String purchaserBankNameAccount;
//
//    /**
//     * 校验码
//     */
//    @TableField("check_code")
//    private String checkCode;
//
//    /**
//     * 机器编码
//     */
//    @TableField("machine_code")
//    private String machineCode;
//
//    /**
//     * 密文
//     */
//    @TableField("cipher_text")
//    private String cipherText;
//
//    /**
//     * 含税金额
//     */
//    @TableField("amount_with_tax")
//    private BigDecimal amountWithTax;
//
//    /**
//     * 税额
//     */
//    @TableField("tax_amount")
//    private BigDecimal taxAmount;
//
//    /**
//     * 不含税金额
//     */
//    @TableField("amount_without_tax")
//    private BigDecimal amountWithoutTax;
//
//    /**
//     * 销方名称
//     */
//    @TableField("seller_name")
//    private String sellerName;
//
//    /**
//     * 销方纳税人识别号
//     */
//    @TableField("seller_tax_no")
//    private String sellerTaxNo;
//
//    /**
//     * 销方地址电话
//     */
//    @TableField("seller_addr_tel")
//    private String sellerAddrTel;
//
//    /**
//     * 销方银行名称账号
//     */
//    @TableField("seller_bank_name_account")
//    private String sellerBankNameAccount;
//
//    /**
//     * 收款人姓名
//     */
//    @TableField("cashier_name")
//    private String cashierName;
//
//    /**
//     * 复核人姓名
//     */
//    @TableField("checker_name")
//    private String checkerName;
//
//    /**
//     * 开票人姓名
//     */
//    @TableField("invoicer_name")
//    private String invoicerName;
//
//    /**
//     * 发票备注
//     */
//    @TableField("remark")
//    private String remark;
//
//    /**
//     * 税率
//     */
//    @TableField("tax_rate")
//    private String taxRate;
//
//    /**
//     * 购方no
//     */
//    @TableField("purchaser_no")
//    private String purchaserNo;
//
//    /**
//     * 销方no
//     */
//    @TableField("seller_no")
//    private String sellerNo;
//
//    /**
//     * 发票状态 0-作废,1-正常,2-红冲,3-失控,4-异常,9-未知
//     */
//    @TableField("status")
//    private String status;
//
//    /**
//     * 红蓝标识 1-蓝字发票 2-红字发票
//     */
//    @TableField("invoice_color")
//    private String invoiceColor;
//
//    /**
//     * 红冲状态 1-未红冲（蓝票）2-部分红冲 3-红冲
//     */
//    @TableField("red_flag")
//    private String redFlag;
//
//    /**
//     * 税务大类：01 增值税专用 02 增值税普通 03 其他 04 进出口类
//     */
//    @TableField("tax_category")
//    private String taxCategory;
//
//    /**
//     * 行业开具类型：10 增值税（常规）20 通行费 21 火车票22 行程单23 客运汽车24 出租车25 过路费30 成品油40 海关缴款通知书50 农产品收购60 机动车61 二手车70 其他发票
//     */
//    @TableField("industry_issue_type")
//    private String industryIssueType;
//
//    /**
//     * 发票介质 01 纸票（常规）02 纸票（卷票）03 纸票（定额）04 纸票（通用机打）05 纸票（其他）06电子（常规）07电子（区块链）
//     */
//    @TableField("invoice_medium")
//    private String invoiceMedium;
//
//    /**
//     * 认证状态：0-待认证1-已认证
//     */
//    @TableField("auth_status")
//    private String authStatus;
//
//    /**
//     * 认证后状态 1-已抵扣2-认证异常3-已转出
//     */
//    @TableField("auth_after_status")
//    private String authAfterStatus;
//
//    /**
//     * 底账勾选状态:1-未勾选 2-已勾选 3-已勾选（签名确认）4-不可勾选
//     */
//    @TableField("auth_sync_status")
//    private String authSyncStatus;
//
//    /**
//     * 认证所属期
//     */
//    @TableField("auth_tax_period")
//    private String authTaxPeriod;
//
//    /**
//     * 抵扣用途(0-默认1-抵扣 2-不抵扣)
//     */
//    @TableField("auth_use")
//    private String authUse;
//
//    /**
//     * 认证业务日期
//     */
//    @TableField("auth_bussi_date")
//    private String authBussiDate;
//
//    /**
//     * 有效税额
//     */
//    @TableField("effective_tax_amount")
//    private BigDecimal effectiveTaxAmount;
//
//    /**
//     * 剩余可匹配的额度 默认与不含税金额相同
//     */
//    @TableField("remaining_amount")
//    private BigDecimal remainingAmount;
//
//    /**
//     * 创建日期
//     */
//    @TableField("create_time")
//    private Date createTime;
//
//    /**
//     * 更新日期
//     */
//    @TableField(value="update_time", update="getdate()" )
//    private Date updateTime;
//
//    @TableField("id")
//    private Long id;
//
//
//    public static final String INVOICE_CODE = "invoice_code";
//
//    public static final String INVOICE_NO = "invoice_no";
//
//    public static final String PAPER_DREW_DATE = "paper_drew_date";
//
//    public static final String PURCHASER_NAME = "purchaser_name";
//
//    public static final String PURCHASER_TAX_NO = "purchaser_tax_no";
//
//    public static final String PURCHASER_ADDR_TEL = "purchaser_addr_tel";
//
//    public static final String PURCHASER_BANK_NAME_ACCOUNT = "purchaser_bank_name_account";
//
//    public static final String CHECK_CODE = "check_code";
//
//    public static final String MACHINE_CODE = "machine_code";
//
//    public static final String CIPHER_TEXT = "cipher_text";
//
//    public static final String AMOUNT_WITH_TAX = "amount_with_tax";
//
//    public static final String TAX_AMOUNT = "tax_amount";
//
//    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";
//
//    public static final String SELLER_NAME = "seller_name";
//
//    public static final String SELLER_TAX_NO = "seller_tax_no";
//
//    public static final String SELLER_ADDR_TEL = "seller_addr_tel";
//
//    public static final String SELLER_BANK_NAME_ACCOUNT = "seller_bank_name_account";
//
//    public static final String CASHIER_NAME = "cashier_name";
//
//    public static final String CHECKER_NAME = "checker_name";
//
//    public static final String INVOICER_NAME = "invoicer_name";
//
//    public static final String REMARK = "remark";
//
//    public static final String TAX_RATE = "tax_rate";
//
//    public static final String PURCHASER_NO = "purchaser_no";
//
//    public static final String SELLER_NO = "seller_no";
//
//    public static final String STATUS = "status";
//
//    public static final String INVOICE_COLOR = "invoice_color";
//
//    public static final String RED_FLAG = "red_flag";
//
//    public static final String TAX_CATEGORY = "tax_category";
//
//    public static final String INDUSTRY_ISSUE_TYPE = "industry_issue_type";
//
//    public static final String INVOICE_MEDIUM = "invoice_medium";
//
//    public static final String AUTH_STATUS = "auth_status";
//
//    public static final String AUTH_AFTER_STATUS = "auth_after_status";
//
//    public static final String AUTH_SYNC_STATUS = "auth_sync_status";
//
//    public static final String AUTH_TAX_PERIOD = "auth_tax_period";
//
//    public static final String AUTH_USE = "auth_use";
//
//    public static final String AUTH_BUSSI_DATE = "auth_bussi_date";
//
//    public static final String EFFECTIVE_TAX_AMOUNT = "effective_tax_amount";
//
//    public static final String REMAINING_AMOUNT = "remaining_amount";
//
//    public static final String CREATE_TIME = "create_time";
//
//    public static final String UPDATE_TIME = "update_time";
//
//    public static final String ID = "id";
//
//}
