package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_settlement")
public class TXfSettlementEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 销方编码，供应商编码
     */
    @TableField("seller_no")
    private String sellerNo;

    /**
     * 供应商名称
     */
    @TableField("seller_name")
    private String sellerName;

    /**
     * 供应商税号
     */
    @TableField("seller_tax_no")
    private String sellerTaxNo;

    /**
     * 供应商编码
     */
    @TableField("seller_tel")
    private String sellerTel;

    /**
     * 供应商地址
     */
    @TableField("seller_address")
    private String sellerAddress;

    /**
     * 供应商开户行名称
     */
    @TableField("seller_bank_name")
    private String sellerBankName;

    /**
     * 供应商银行账号
     */
    @TableField("seller_bank_account")
    private String sellerBankAccount;

    /**
     * 购方编码
     */
    @TableField("purchaser_no")
    private String purchaserNo;

    /**
     * 购方名称
     */
    @TableField("purchaser_name")
    private String purchaserName;

    /**
     * 购方税号
     */
    @TableField("purchaser_tax_no")
    private String purchaserTaxNo;

    /**
     * 购方电话
     */
    @TableField("purchaser_tel")
    private String purchaserTel;

    /**
     * 购方地址
     */
    @TableField("purchaser_address")
    private String purchaserAddress;

    /**
     * 购方开户行
     */
    @TableField("purchaser_bank_name")
    private String purchaserBankName;

    /**
     * 购方银行账号
     */
    @TableField("purchaser_bank_account")
    private String purchaserBankAccount;

    /**
     * 发票类型  专纸：01
机动车：03
普纸：04
普电：10
卷票：11
通行费电票：14
专电：08
     */
    @TableField("invoice_type")
    private String invoiceType;

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
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 可匹配余额
     */
    @TableField("available_amount")
    private BigDecimal availableAmount;

    /**
     * 批次号（沃尔玛数据同步批次）
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 结算单编码
     */
    @TableField("settlement_no")
    private String settlementNo;

    /**
     * 结算单类型:协议单 2 EPD 3
     */
    @TableField("settlement_type")
    private Integer settlementType;

    /**
     * 结算单状态
待确认 1
待开票 2
已开部票 3
已开票 4
已完成 5
待审核 6
已撤销 7

     */
    @TableField("settlement_status")
    private Integer settlementStatus;

    /**
     * 税率 目前整数存储，需要程序单独处理
1---1%
9---9%

     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    @TableField("update_user")
    private Long updateUser;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("create_time")
    private Date createTime;

    @TableField("price_method")
    private Integer priceMethod;

    @TableField(value="update_time", update="now(3)" )
    private Date updateTime;

    @TableField("create_user")
    private Long createUser;


    public static final String SELLER_NO = "seller_no";

    public static final String SELLER_NAME = "seller_name";

    public static final String SELLER_TAX_NO = "seller_tax_no";

    public static final String SELLER_TEL = "seller_tel";

    public static final String SELLER_ADDRESS = "seller_address";

    public static final String SELLER_BANK_NAME = "seller_bank_name";

    public static final String SELLER_BANK_ACCOUNT = "seller_bank_account";

    public static final String PURCHASER_NO = "purchaser_no";

    public static final String PURCHASER_NAME = "purchaser_name";

    public static final String PURCHASER_TAX_NO = "purchaser_tax_no";

    public static final String PURCHASER_TEL = "purchaser_tel";

    public static final String PURCHASER_ADDRESS = "purchaser_address";

    public static final String PURCHASER_BANK_NAME = "purchaser_bank_name";

    public static final String PURCHASER_BANK_ACCOUNT = "purchaser_bank_account";

    public static final String INVOICE_TYPE = "invoice_type";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String REMARK = "remark";

    public static final String AVAILABLE_AMOUNT = "available_amount";

    public static final String BATCH_NO = "batch_no";

    public static final String SETTLEMENT_NO = "settlement_no";

    public static final String SETTLEMENT_TYPE = "settlement_type";

    public static final String SETTLEMENT_STATUS = "settlement_status";

    public static final String TAX_RATE = "tax_rate";

    public static final String UPDATE_USER = "update_user";

    public static final String ID = "id";

    public static final String CREATE_TIME = "create_time";

    public static final String PRICE_METHOD = "price_method";

    public static final String UPDATE_TIME = "update_time";

    public static final String CREATE_USER = "create_user";

}
