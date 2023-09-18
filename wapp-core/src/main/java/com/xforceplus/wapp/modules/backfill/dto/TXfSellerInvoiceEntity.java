package com.xforceplus.wapp.modules.backfill.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * <p>
    * 发票主表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-11-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_seller_invoice")
public class TXfSellerInvoiceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 结算单序号
     */
    private Long settlementId;

    /**
     * 结算单号（企业）
     */
    private String settlementNo;

    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * jv号
     */
    private String jvCode;

    /**
     * 所属期
     */
    private String relevancyPeriod;

    /**
     * 销方租户代码
     */
    private String sellerTenantCode;

    /**
     * 销方租户公司编号
     */
    private String sellerNo;

    /**
     * 购方租户定义的销方公司编号
     */
    private String sellerNoFromPurchaser;

    /**
     * 销方公司代码
     */
    private String sellerCode;

    /**
     * 销方纳税人识别号
     */
    private String sellerTaxNo;

    /**
     * 销方名称
     */
    private String sellerName;

    /**
     * 销方地址电话
     */
    private String sellerAddrTel;

    /**
     * 销方地址
     */
    private String sellerAddress;

    /**
     * 销方电话
     */
    private String sellerTel;

    /**
     * 销方银行名称账号
     */
    private String sellerBankInfo;

    /**
     * 销方银行名称
     */
    private String sellerBankName;

    /**
     * 销方银行账号
     */
    private String sellerBankAccount;

    /**
     * 购方租户代码
     */
    private String purchaserTenantCode;

    /**
     * 购方租户公司编号
     */
    private String purchaserNo;

    /**
     * 销方租户定义的购方公司编号
     */
    private String purchaserNoFromSeller;

    /**
     * 购方公司代码
     */
    private String purchaserCode;

    /**
     * 购方纳税人识别号
     */
    private String purchaserTaxNo;

    /**
     * 购方名称
     */
    private String purchaserName;

    /**
     * 购方地址电话
     */
    private String purchaserAddrTel;

    /**
     * 购方地址
     */
    private String purchaserAddress;

    /**
     * 购方电话
     */
    private String purchaserTel;

    /**
     * 购方银行名称账号
     */
    private String purchaserBankInfo;

    /**
     * 购方银行名称
     */
    private String purchaserBankName;

    /**
     * 购方银行账号
     */
    private String purchaserBankAccount;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    private BigDecimal amountWithoutTax;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 含税金额
     */
    @TableField("amount_with_tax")
    private BigDecimal amountWithTax;

    /**
     * 发票开票日期
     */
    private String paperDrewDate;

    /**
     * 发票来源1-抽取5-直连 7-回填
     */
    private String invoiceOrig;

    /**
     * 二维码发票标记
     */
    private String twoCodeFlag;

    /**
     * 二维密文
     */
    private String cipherTextTwoCode;

    /**
     * 密文
     */
    private String cipherText;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收款人姓名
     */
    private String cashierName;

    /**
     * 复核人姓名
     */
    private String checkerName;

    /**
     * 开票人姓名
     */
    private String invoicerName;

    /**
     * 机器编码
     */
    private String machineCode;

    /**
     * 导入时间待确认
     */
    private String importTime;

    /**
     * 导入操作账号待确认
     */
    private String importUserId;

    /**
     * 抽取时间
     */
    private String drawoutTime;

    /**
     * 抽取操作账号
     */
    private String drawoutUserId;

    /**
     * 是否需要认证1-需要2-不需要
     */
    private String isNeedAuth;

    /**
     * 运单号物流模块使用
     */
    private String expressNo;

    /**
     * 虚拟标志
     */
    private String virtualFlag;

    /**
     * 发票状态1-正常 0-作废 9-删除
     */
    private String status;

    /**
     * 原发票号码
     */
    private String originInvoiceNo;

    /**
     * 原发票代码
     */
    private String originInvoiceCode;

    /**
     * 红字信息表编号
     */
    private String redNotificationNo;

    /**
     * 红冲时间
     */
    private String redTime;

    /**
     * 红冲状态1-待红冲2-待部分红冲3-红冲4-部分红冲
     */
    private String redFlag;

    /**
     * 发票明细生成方式
     */
    private String invoiceItemMode;

    /**
     * 处理标记
     */
    private String processFlag;

    /**
     * 处理备注
     */
    private String processRemark;

    /**
     * 抽取关联接口表id
     */
    private String drawoutInterfaceId;

    /**
     * 抽取状态
     */
    private String drawoutStatus;

    /**
     * 电子发票标志1-电子发票
     */
    private String electronicInvoiceFlag;

    /**
     * 系统来源
     */
    private String systemOrig;

    /**
     * 销货清单文件打印标志（0-否,1-是）
     */
    private String saleListFileFlag;

    /**
     * 销货清单打印备注
     */
    private String saleListFileRemark;

    /**
     * 校验码
     */
    private String checkCode;

    /**
     * 适用业务单据类型
     */
    private String businessBillType;

    /**
     * 是否已上传附件标志0-未上传1-已上传
     */
    private String attachmentFlag;

    /**
     * 销项接口表id
     */
    private String invoiceInterfaceId;

    /**
     * 协同类型0-非协同；1-协同；空-以票易通平台配置决定
     */
    private String cooperateFlag;

    /**
     * 打印内容标志0-打印单价和数量1-不打印单价和数量,默认0
     */
    private String printContentFlag;

    /**
     * 特殊扩展字段1待确认
     */
    private String specialExt1;

    /**
     * 特殊扩展字段2待确认
     */
    private String specialExt2;

    /**
     * 作废时间
     */
    private String deposeTime;

    /**
     * 作废操作账号
     */
    private String deposeUserId;

    /**
     * 删除时间
     */
    private String deleteTime;

    /**
     * 删除操作账号
     */
    private String deleteUserId;

    /**
     * 退回时间
     */
    private String retreatTime;

    /**
     * 退回说明
     */
    private String retreatRemark;

    /**
     * 退回状态0：未退回；1：系统退回；2：购方退回
     */
    private String retreatStatus;

    /**
     * 平台预制发票正式信息回填时间
     */
    private String createTime;

    /**
     * 平台预制发票正式信息回填操作账号
     */
    private String createUserId;

    /**
     * 更新账号
     */
    private String updateUserId;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 处理状态1-正常 2-待处理 0-作废
     */
    private String handleStatus;

    /**
     * pdf路径
     */
    private String pdfPath;

    /**
     * 运维状态1-运维状态
     */
    private String opStatus;

    /**
     * 进项发票序列号待确认
     */
    private String purchaserInvoiceId;

    /**
     * 处理状态
     */
    private String handleFlag;

    /**
     * 处理时间
     */
    private String handleTime;

    /**
     * 运维状态1-运维状态
     */
    private String handleRemark;

    /**
     * 可疑标签（0：可疑，1：正常）
     */
    private String isSureFlag;

    /**
     * 可疑标注
     */
    private String isSureRemark;

    /**
     * 开票点名称
     */
    private String makeOutUnitName;

    /**
     * 开票点代码
     */
    private String makeOutUnitCode;

    /**
     * 抵扣联影像路径
     */
    private String scanDeductionImageUrl;

    /**
     * 发票联影像路径
     */
    private String scanInvoiceImageUrl;

    private String ext1;

    private String ext2;

    private String ext3;

    private String ext4;

    private String ext5;

    private String ext6;

    /**
     * 扩展字段7，清单标识，1详见清单，0打印清单，抽取发票用
     */
    @TableField("ext7")
    private String ext7;

    /**
     * 扩展字段8，打印标志，1已打印，0未打印
     */
    private String ext8;

    private String ext9;

    private String ext10;

    /**
     * 折扣模式
     */
    private String discountMode;

    /**
     * 接收者（开完发票后邮件通知的邮箱）
     */
    private String reciveUser;

    /**
     * 税控终端码
     */
    private String taxControlCode;

    /**
     * 预作废状态：1-待作废 2-作废驳回 3-作废成功\r\n4-申请作废驳回 5-申请红冲驳回 7-申请作废或红冲\r\n8 - 申请作废或红冲驳回
     */
    private String invalidateFlag;

    /**
     * 开具模式：client/server/blockchain/vukey
     */
    private String mode;

    /**
     * 开票机号
     */
    private String machineNo;

    /**
     * 匹配状态；0:未匹配；1：已匹配
     */
    private Integer matchStatus;


    private String eUrl;

    public static final String ID = "id";

    public static final String SETTLEMENT_ID = "settlement_id";

    public static final String SETTLEMENT_NO = "settlement_no";

    public static final String INVOICE_TYPE = "invoice_type";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String INVOICE_NO = "invoice_no";

    public static final String JV_CODE = "jv_code";

    public static final String RELEVANCY_PERIOD = "relevancy_period";

    public static final String SELLER_TENANT_CODE = "seller_tenant_code";

    public static final String SELLER_NO = "seller_no";

    public static final String SELLER_NO_FROM_PURCHASER = "seller_no_from_purchaser";

    public static final String SELLER_CODE = "seller_code";

    public static final String SELLER_TAX_NO = "seller_tax_no";

    public static final String SELLER_NAME = "seller_name";

    public static final String SELLER_ADDR_TEL = "seller_addr_tel";

    public static final String SELLER_ADDRESS = "seller_address";

    public static final String SELLER_TEL = "seller_tel";

    public static final String SELLER_BANK_INFO = "seller_bank_info";

    public static final String SELLER_BANK_NAME = "seller_bank_name";

    public static final String SELLER_BANK_ACCOUNT = "seller_bank_account";

    public static final String PURCHASER_TENANT_CODE = "purchaser_tenant_code";

    public static final String PURCHASER_NO = "purchaser_no";

    public static final String PURCHASER_NO_FROM_SELLER = "purchaser_no_from_seller";

    public static final String PURCHASER_CODE = "purchaser_code";

    public static final String PURCHASER_TAX_NO = "purchaser_tax_no";

    public static final String PURCHASER_NAME = "purchaser_name";

    public static final String PURCHASER_ADDR_TEL = "purchaser_addr_tel";

    public static final String PURCHASER_ADDRESS = "purchaser_address";

    public static final String PURCHASER_TEL = "purchaser_tel";

    public static final String PURCHASER_BANK_INFO = "purchaser_bank_info";

    public static final String PURCHASER_BANK_NAME = "purchaser_bank_name";

    public static final String PURCHASER_BANK_ACCOUNT = "purchaser_bank_account";

    public static final String TAX_RATE = "tax_rate";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String PAPER_DREW_DATE = "paper_drew_date";

    public static final String INVOICE_ORIG = "invoice_orig";

    public static final String TWO_CODE_FLAG = "two_code_flag";

    public static final String CIPHER_TEXT_TWO_CODE = "cipher_text_two_code";

    public static final String CIPHER_TEXT = "cipher_text";

    public static final String REMARK = "remark";

    public static final String CASHIER_NAME = "cashier_name";

    public static final String CHECKER_NAME = "checker_name";

    public static final String INVOICER_NAME = "invoicer_name";

    public static final String MACHINE_CODE = "machine_code";

    public static final String IMPORT_TIME = "import_time";

    public static final String IMPORT_USER_ID = "import_user_id";

    public static final String DRAWOUT_TIME = "drawout_time";

    public static final String DRAWOUT_USER_ID = "drawout_user_id";

    public static final String IS_NEED_AUTH = "is_need_auth";

    public static final String EXPRESS_NO = "express_no";

    public static final String VIRTUAL_FLAG = "virtual_flag";

    public static final String STATUS = "status";

    public static final String ORIGIN_INVOICE_NO = "origin_invoice_no";

    public static final String ORIGIN_INVOICE_CODE = "origin_invoice_code";

    public static final String RED_NOTIFICATION_NO = "red_notification_no";

    public static final String RED_TIME = "red_time";

    public static final String RED_FLAG = "red_flag";

    public static final String INVOICE_ITEM_MODE = "invoice_item_mode";

    public static final String PROCESS_FLAG = "process_flag";

    public static final String PROCESS_REMARK = "process_remark";

    public static final String DRAWOUT_INTERFACE_ID = "drawout_interface_id";

    public static final String DRAWOUT_STATUS = "drawout_status";

    public static final String ELECTRONIC_INVOICE_FLAG = "electronic_invoice_flag";

    public static final String SYSTEM_ORIG = "system_orig";

    public static final String SALE_LIST_FILE_FLAG = "sale_list_file_flag";

    public static final String SALE_LIST_FILE_REMARK = "sale_list_file_remark";

    public static final String CHECK_CODE = "check_code";

    public static final String BUSINESS_BILL_TYPE = "business_bill_type";

    public static final String ATTACHMENT_FLAG = "attachment_flag";

    public static final String INVOICE_INTERFACE_ID = "invoice_interface_id";

    public static final String COOPERATE_FLAG = "cooperate_flag";

    public static final String PRINT_CONTENT_FLAG = "print_content_flag";

    public static final String SPECIAL_EXT1 = "special_ext1";

    public static final String SPECIAL_EXT2 = "special_ext2";

    public static final String DEPOSE_TIME = "depose_time";

    public static final String DEPOSE_USER_ID = "depose_user_id";

    public static final String DELETE_TIME = "delete_time";

    public static final String DELETE_USER_ID = "delete_user_id";

    public static final String RETREAT_TIME = "retreat_time";

    public static final String RETREAT_REMARK = "retreat_remark";

    public static final String RETREAT_STATUS = "retreat_status";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_USER_ID = "create_user_id";

    public static final String UPDATE_USER_ID = "update_user_id";

    public static final String UPDATE_TIME = "update_time";

    public static final String HANDLE_STATUS = "handle_status";

    public static final String PDF_PATH = "pdf_path";

    public static final String OP_STATUS = "op_status";

    public static final String PURCHASER_INVOICE_ID = "purchaser_invoice_id";

    public static final String HANDLE_FLAG = "handle_flag";

    public static final String HANDLE_TIME = "handle_time";

    public static final String HANDLE_REMARK = "handle_remark";

    public static final String IS_SURE_FLAG = "is_sure_flag";

    public static final String IS_SURE_REMARK = "is_sure_remark";

    public static final String MAKE_OUT_UNIT_NAME = "make_out_unit_name";

    public static final String MAKE_OUT_UNIT_CODE = "make_out_unit_code";

    public static final String SCAN_DEDUCTION_IMAGE_URL = "scan_deduction_image_url";

    public static final String SCAN_INVOICE_IMAGE_URL = "scan_invoice_image_url";

    public static final String EXT1 = "ext1";

    public static final String EXT2 = "ext2";

    public static final String EXT3 = "ext3";

    public static final String EXT4 = "ext4";

    public static final String EXT5 = "ext5";

    public static final String EXT6 = "ext6";

    public static final String EXT7 = "ext7";

    public static final String EXT8 = "ext8";

    public static final String EXT9 = "ext9";

    public static final String EXT10 = "ext10";

    public static final String DISCOUNT_MODE = "discount_mode";

    public static final String RECIVE_USER = "recive_user";

    public static final String TAX_CONTROL_CODE = "tax_control_code";

    public static final String INVALIDATE_FLAG = "invalidate_flag";

    public static final String MODE = "mode";

    public static final String MACHINE_NO = "machine_no";

    public static final String MATCH_STATUS = "match_status";

}
