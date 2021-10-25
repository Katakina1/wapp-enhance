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
    * 底账表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_dx_record_invoice")
public class TDxRecordInvoiceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
     */
    @TableField("invoice_type")
    private String invoiceType;

    /**
     * 发票代码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 发票号码
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 开票日期
     */
    @TableField("invoice_date")
    private Date invoiceDate;

    /**
     * 购方税号
     */
    @TableField("gf_tax_no")
    private String gfTaxNo;

    /**
     * 购方名称
     */
    @TableField("gf_name")
    private String gfName;

    /**
     * 购方地址电话
     */
    @TableField("gf_address_and_phone")
    private String gfAddressAndPhone;

    /**
     * 购方开户行及账号
     */
    @TableField("gf_bank_and_no")
    private String gfBankAndNo;

    /**
     * 销方税号
     */
    @TableField("xf_tax_no")
    private String xfTaxNo;

    /**
     * 销方名称
     */
    @TableField("xf_name")
    private String xfName;

    /**
     * 销方地址及电话
     */
    @TableField("xf_address_and_phone")
    private String xfAddressAndPhone;

    /**
     * 销方开户行及账号
     */
    @TableField("xf_bank_and_no")
    private String xfBankAndNo;

    /**
     * 金额
     */
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 价格合计
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常 5-蓝冲
     */
    @TableField("invoice_status")
    private String invoiceStatus;

    /**
     * 发票状态修改时间
     */
    @TableField("status_update_date")
    private Date statusUpdateDate;

    /**
     * 发票状态最后修改时间
     */
    @TableField("last_update_date")
    private Date lastUpdateDate;

    /**
     * 认证时间
     */
    @TableField("rzh_date")
    private Date rzhDate;

    /**
     * 签收时间
     */
    @TableField("qs_date")
    private Date qsDate;

    /**
     * 最晚认证归属期 yyyyMM
     */
    @TableField("rzh_belong_date_late")
    private String rzhBelongDateLate;

    /**
     * 实际认证归属期 yyyyMM
     */
    @TableField("rzh_belong_date")
    private String rzhBelongDate;

    /**
     * 认证确认时间
     */
    @TableField("confirm_date")
    private Date confirmDate;

    /**
     * 认证方式 1-勾选认证 2-扫描认证
     */
    @TableField("rzh_type")
    private String rzhType;

    /**
     * 是否认证 0-未认证 1-已认证
     */
    @TableField("rzh_yesorno")
    private String rzhYesorno;

    /**
     * 提交认证操作人账号
     */
    @TableField("gx_user_account")
    private String gxUserAccount;

    /**
     * 提交认证操作人
     */
    @TableField("gx_user_name")
    private String gxUserName;

    /**
     * 底账来源  0-采集 1-查验 2-录入
     */
    @TableField("source_system")
    private String sourceSystem;

    /**
     * 是否有效 1-有效 0-无效
     */
    @TableField("valid")
    private String valid;

    /**
     * 发票代码+发票号码    唯一索引 防重复
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 采集时间
     */
    @TableField("create_date")
    private Date createDate;

    /**
     * 勾选时间
     */
    @TableField("gx_date")
    private Date gxDate;

    /**
     * 认证结果失败错误码
     */
    @TableField("rzh_back_code")
    private String rzhBackCode;

    /**
     * 认证结果回传信息
     */
    @TableField("rzh_back_msg")
    private String rzhBackMsg;

    /**
     * 当前税款所属期
     */
    @TableField("dqskssq")
    private String dqskssq;

    /**
     * 当前税款所属期勾选截止日
     */
    @TableField("gxjzr")
    private String gxjzr;

    /**
     * 当前税款所属期可勾选发票开票日期范围起
     */
    @TableField("gxfwq")
    private String gxfwq;

    /**
     * 当前税款所属期可勾选发票开票日期范围止
     */
    @TableField("gxfwz")
    private String gxfwz;

    /**
     * 是否已勾选
     */
    @TableField("sfygx")
    private String sfygx;

    /**
     * 是否存入明细 0 无明细 1 有明细
     */
    @TableField("detail_yesorno")
    private String detailYesorno;

    /**
     * 勾选方式(0-手工勾选 1-扫码勾选 2-导入勾选 3- 智能勾选 4-手工认证 5-扫码认证 6-导入认证)
     */
    @TableField("gx_type")
    private String gxType;

    /**
     * 认证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
     */
    @TableField("auth_status")
    private String authStatus;

    /**
     * 发送认证时间
     */
    @TableField("send_date")
    private Date sendDate;

    /**
     * 认证类型(1-抵扣 2-退税 3-代理退税   发票已认证、已勾选有该标签）
     */
    @TableField("rzlx")
    private String rzlx;

    /**
     * 是否代办退税(0：否 1：是)
     */
    @TableField("sfdbts")
    private String sfdbts;

    /**
     * 签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收5-pdf上传签收
     */
    @TableField("qs_type")
    private String qsType;

    /**
     * 签收结果（0-未签收 1-已签收）
     */
    @TableField("qs_status")
    private String qsStatus;

    /**
     * 校验码
     */
    @TableField("check_code")
    private String checkCode;

    /**
     * 通行费标志(Y-可抵扣通行费，N-不可抵扣通行费)
     */
    @TableField("txfbz")
    private String txfbz;

    /**
     * 零税率标志 (空:非零税率，1:税率栏位显示“免税”，2:税率栏位显示“不征收”，3:零税率
     */
    @TableField("lslbz")
    private String lslbz;

    /**
     * 转出状态 0-未转出   1-全部转出  2-部分转出
     */
    @TableField("out_status")
    private String outStatus;

    /**
     * 转出金额
     */
    @TableField("out_invoice_amout")
    private BigDecimal outInvoiceAmout;

    /**
     * 转出税额
     */
    @TableField("out_tax_amount")
    private BigDecimal outTaxAmount;

    /**
     * 转出原因1-免税项目用 ；2-集体福利,个人消费；3-非正常损失；4-简易计税方法征税项目用；5-免抵退税办法不得抵扣的进项税额；6-纳税检查调减进项税额；7-红字专用发票通知单注明的进项税额；8-上期留抵税额抵减欠税
     */
    @TableField("out_reason")
    private String outReason;

    /**
     * 转出备注
     */
    @TableField("out_remark")
    private String outRemark;

    /**
     * 转出日期
     */
    @TableField("out_date")
    private Date outDate;

    /**
     * 转出人
     */
    @TableField("out_by")
    private String outBy;

    /**
     * 确认人
     */
    @TableField("confirm_user")
    private String confirmUser;

    /**
     * 机器编号
     */
    @TableField("machinecode")
    private String machinecode;

    /**
     * 发票状态更新字段-- 用于接口
     */
    @TableField("update_interface")
    private Date updateInterface;

    /**
     * 申请的认证类型
     */
    @TableField("apply_rzlx")
    private String applyRzlx;

    /**
     * 申请认证税款所属期
     */
    @TableField("apply_tax_period")
    private String applyTaxPeriod;

    /**
     * 代开标志
     */
    @TableField("commission_type")
    private String commissionType;

    /**
     * 代开税号
     */
    @TableField("commission_tax")
    private String commissionTax;

    /**
     * 代开企业名称
     */
    @TableField("commission_name")
    private String commissionName;

    /**
     * 采集税号
     */
    @TableField("new_gf_taxno")
    private String newGfTaxno;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 发票信息
     */
    @TableField("invoice_information")
    private String invoiceInformation;

    /**
     * 发票处理状态
     */
    @TableField("invoice_deal_status")
    private String invoiceDealStatus;

    /**
     * 大象平台匹配状态
     */
    @TableField("dxhy_match_status")
    private String dxhyMatchStatus;

    /**
     * 结算金额
     */
    @TableField("settlementAmount")
    private BigDecimal settlementAmount;

    /**
     * 帐务调整金额(发票金额-结算金额)
     */
    @TableField("account_adjustment_amount")
    private BigDecimal accountAdjustmentAmount;

    /**
     * 匹配关联号
     */
    @TableField("matchno")
    private String matchno;

    /**
     * 供应商号
     */
    @TableField("venderid")
    private String venderid;

    /**
     * 供应商税号
     */
    @TableField("vendertaxno")
    private String vendertaxno;

    /**
     * 抵扣税额（0税率普票专用）
     */
    @TableField("deductible_tax")
    private BigDecimal deductibleTax;

    /**
     * 抵扣税率（0税率普票专用）
     */
    @TableField("deductible_tax_rate")
    private BigDecimal deductibleTaxRate;

    /**
     * 装订号
     */
    @TableField("bbindingno")
    private String bbindingno;

    /**
     * 装箱号
     */
    @TableField("packingno")
    private String packingno;

    /**
     * 退单号
     */
    @TableField("rebateno")
    private String rebateno;

    /**
     * 退单快递号
     */
    @TableField("rebate_expressno")
    private String rebateExpressno;

    /**
     * 扫描流水号
     */
    @TableField("scanning_seriano")
    private String scanningSeriano;

    /**
     * 打印号
     */
    @TableField("printcode")
    private String printcode;

    /**
     * 凭证号
     */
    @TableField("certificate_no")
    private String certificateNo;

    /**
     * 扫描流水号
     */
    @TableField("invoice_serial_no")
    private String invoiceSerialNo;

    /**
     * orgcode
     */
    @TableField("jvcode")
    private String jvcode;

    /**
     * 供应商名称
     */
    @TableField("vendername")
    private String vendername;

    /**
     * 生成退单日期
     */
    @TableField("rebate_date")
    private Date rebateDate;

    /**
     * 匹配日期
     */
    @TableField("match_date")
    private Date matchDate;

    /**
     * host状态（0，1：loaded;13:invoice delete;14 invoice reactived;10:unmatched;12matched out;11:matched equal;15:reconciled(manual);19:reconciled(auto);9:extracted for payment;99:paid;999:purged;
     */
    @TableField("host_status")
    private String hostStatus;

    /**
     * 是否生成退单号（0-未生成，1-已生成）
     */
    @TableField("rebateyesorno")
    private String rebateyesorno;

    /**
     * 是否录入邮包号（0-未录入，1-已录入）
     */
    @TableField("expressnoyesorno")
    private String expressnoyesorno;

    /**
     * 装订日期
     */
    @TableField("bbinding_date")
    private Date bbindingDate;

    /**
     * 是否s生成装订号（0-未生成，1-已生成）
     */
    @TableField("bindyesorno")
    private String bindyesorno;

    /**
     * 是否录入装箱号（0-未录入，1-已录入）
     */
    @TableField("packyesorno")
    private String packyesorno;

    /**
     * 可红冲金额
     */
    @TableField("red_money_amount")
    private BigDecimal redMoneyAmount;

    /**
     * 付款金额
     */
    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    /**
     * 费用号
     */
    @TableField("cost_no")
    private String costNo;

    /**
     * 红字通知单号
     */
    @TableField("red_notice_number")
    private String redNoticeNumber;

    /**
     * 扫描匹配状态（0-未匹配，1-匹配成功，2匹配失败）
     */
    @TableField("scan_match_status")
    private String scanMatchStatus;

    /**
     * 是否借阅(0-未借阅，1-已被借阅)
     */
    @TableField("borrowyesorno")
    private String borrowyesorno;

    /**
     * 扫描匹配日期
     */
    @TableField("scan_match_date")
    private Date scanMatchDate;

    /**
     * 装箱日期
     */
    @TableField("packing_date")
    private Date packingDate;

    /**
     * 借阅日期
     */
    @TableField("borrow_date")
    private Date borrowDate;

    /**
     * 申请编号 yyyyMM+4位流水号
     */
    @TableField("application_number")
    private String applicationNumber;

    /**
     * 已冲销金额
     */
    @TableField("covered_amount")
    private BigDecimal coveredAmount;

    /**
     * HOST回写时间
     */
    @TableField("host_date")
    private Date hostDate;

    /**
     * 发票流程类型（1：商品，2：费用）
     */
    @TableField("flow_type")
    private String flowType;

    /**
     * orgname
     */
    @TableField("jvname")
    private String jvname;

    /**
     * 凭证抬头文本
     */
    @TableField("document_header_text")
    private String documentHeaderText;

    /**
     * 凭证类型
     */
    @TableField("document_type")
    private String documentType;

    /**
     * 冲销清账
     */
    @TableField("write_off")
    private String writeOff;

    /**
     * 清账凭证
     */
    @TableField("clearance_voucher")
    private String clearanceVoucher;

    /**
     * 清账日期
     */
    @TableField("clearing_date")
    private Date clearingDate;

    /**
     * 显示货币的金额
     */
    @TableField("show_currency_amount")
    private BigDecimal showCurrencyAmount;

    /**
     * 凭证货币
     */
    @TableField("voucher_currency")
    private String voucherCurrency;

    /**
     * 本币
     */
    @TableField("currency")
    private String currency;

    /**
     * 过账日期
     */
    @TableField("posting_date")
    private Date postingDate;

    /**
     * 付款日期
     */
    @TableField("payment_date")
    private Date paymentDate;

    /**
     * 税码
     */
    @TableField("tax_code")
    private String taxCode;

    /**
     * 会计年度
     */
    @TableField("fiscal_year")
    private String fiscalYear;

    /**
     * 文本
     */
    @TableField("invoice_text")
    private String invoiceText;

    /**
     * 科目
     */
    @TableField("subject")
    private String subject;

    /**
     * 公司代码
     */
    @TableField("company_code")
    private String companyCode;

    /**
     * 抵扣发票金额
     */
    @TableField("dk_invoiceAmount")
    private BigDecimal dkInvoiceamount;

    /**
     * 退票状态（0：初始，1：待退票，2：已退票）
     */
    @TableField("tp_status")
    private String tpStatus;

    /**
     * 租金0 代表未匹配，1代表匹配成功
     */
    @TableField("matching")
    private String matching;

    /**
     * sap时间
     */
    @TableField("sap_date")
    private Date sapDate;

    /**
     * 期间
     */
    @TableField("period")
    private String period;

    /**
     * 店号
     */
    @TableField("shop_no")
    private String shopNo;

    /**
     * 匹配时间
     */
    @TableField("matching_date")
    private Date matchingDate;

    /**
     * 是否删除 0-未删除 1--已删除
     */
    @TableField("is_del")
    private String isDel;

    /**
     * 删除时间
     */
    @TableField("del_date")
    private Date delDate;

    /**
     * BPMS付款状态 0--未付款 1-已付款
     */
    @TableField("bpms_pay_status")
    private String bpmsPayStatus;

    /**
     * 成本中心号
     */
    @TableField("cost_dept_id")
    private String costDeptId;

    /**
     * 1不进手工认证
     */
    @TableField("no_deduction")
    private String noDeduction;

    /**
     * 租赁匹配是否成功：0未成功1成功
     */
    @TableField("fixed_matching")
    private String fixedMatching;

    /**
     * 租赁匹配日期
     */
    @TableField("fixed_matching_date")
    private Date fixedMatchingDate;

    /**
     * 租赁taxcode
     */
    @TableField("fixed_tax_code")
    private String fixedTaxCode;

    /**
     * 10位供应商号 
     */
    @TableField("supplier_number")
    private String supplierNumber;

    /**
     * 装箱存放地址
     */
    @TableField("packing_address")
    private String packingAddress;

    /**
     * 结算单号
     */
    @TableField("settlementNo")
    private String settlementNo;

    /**
     * 密文
     */
    @TableField("cipher_text")
    private String cipherText;

    /**
     * 销货清单标志  1-有销货清单
     */
    @TableField("goods_list_flag")
    private String goodsListFlag;

    /**
     * 蓝票剩余可用金额；默认值同invoice_amount
     */
    @TableField("remaining_amount")
    private BigDecimal remainingAmount;

    @TableField("host_taxRate")
    private BigDecimal hostTaxrate;

    @TableField("is_gl")
    private String isGl;

    @TableField("yxse")
    private BigDecimal yxse;

    @TableField("scan_fail_reason")
    private String scanFailReason;

    @TableField("confirm_reason")
    private String confirmReason;

    @TableField("borrow_return_date")
    private Date borrowReturnDate;

    @TableField("is_return_ticket")
    private String isReturnTicket;

    @TableField("borrow_dept")
    private String borrowDept;

    @TableField("category2")
    private String category2;

    @TableField("yqkgxbz")
    private String yqkgxbz;

    @TableField("borrow_reason")
    private String borrowReason;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("eps_no")
    private String epsNo;

    @TableField("confirm_status")
    private String confirmStatus;

    @TableField("confirm_user_id")
    private Long confirmUserId;

    @TableField("category1")
    private String category1;

    @TableField("xxly")
    private String xxly;

    @TableField("confirm_time")
    private Date confirmTime;

    @TableField("ariba_confirm_status")
    private String aribaConfirmStatus;

    @TableField("borrow_return_user")
    private String borrowReturnUser;

    @TableField("borrow_user")
    private String borrowUser;

    @TableField("glzt")
    private String glzt;

    @TableField("sap")
    private String sap;


    public static final String INVOICE_TYPE = "invoice_type";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String INVOICE_NO = "invoice_no";

    public static final String INVOICE_DATE = "invoice_date";

    public static final String GF_TAX_NO = "gf_tax_no";

    public static final String GF_NAME = "gf_name";

    public static final String GF_ADDRESS_AND_PHONE = "gf_address_and_phone";

    public static final String GF_BANK_AND_NO = "gf_bank_and_no";

    public static final String XF_TAX_NO = "xf_tax_no";

    public static final String XF_NAME = "xf_name";

    public static final String XF_ADDRESS_AND_PHONE = "xf_address_and_phone";

    public static final String XF_BANK_AND_NO = "xf_bank_and_no";

    public static final String INVOICE_AMOUNT = "invoice_amount";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String TOTAL_AMOUNT = "total_amount";

    public static final String REMARK = "remark";

    public static final String INVOICE_STATUS = "invoice_status";

    public static final String STATUS_UPDATE_DATE = "status_update_date";

    public static final String LAST_UPDATE_DATE = "last_update_date";

    public static final String RZH_DATE = "rzh_date";

    public static final String QS_DATE = "qs_date";

    public static final String RZH_BELONG_DATE_LATE = "rzh_belong_date_late";

    public static final String RZH_BELONG_DATE = "rzh_belong_date";

    public static final String CONFIRM_DATE = "confirm_date";

    public static final String RZH_TYPE = "rzh_type";

    public static final String RZH_YESORNO = "rzh_yesorno";

    public static final String GX_USER_ACCOUNT = "gx_user_account";

    public static final String GX_USER_NAME = "gx_user_name";

    public static final String SOURCE_SYSTEM = "source_system";

    public static final String VALID = "valid";

    public static final String UUID = "uuid";

    public static final String CREATE_DATE = "create_date";

    public static final String GX_DATE = "gx_date";

    public static final String RZH_BACK_CODE = "rzh_back_code";

    public static final String RZH_BACK_MSG = "rzh_back_msg";

    public static final String DQSKSSQ = "dqskssq";

    public static final String GXJZR = "gxjzr";

    public static final String GXFWQ = "gxfwq";

    public static final String GXFWZ = "gxfwz";

    public static final String SFYGX = "sfygx";

    public static final String DETAIL_YESORNO = "detail_yesorno";

    public static final String GX_TYPE = "gx_type";

    public static final String AUTH_STATUS = "auth_status";

    public static final String SEND_DATE = "send_date";

    public static final String RZLX = "rzlx";

    public static final String SFDBTS = "sfdbts";

    public static final String QS_TYPE = "qs_type";

    public static final String QS_STATUS = "qs_status";

    public static final String CHECK_CODE = "check_code";

    public static final String TXFBZ = "txfbz";

    public static final String LSLBZ = "lslbz";

    public static final String OUT_STATUS = "out_status";

    public static final String OUT_INVOICE_AMOUT = "out_invoice_amout";

    public static final String OUT_TAX_AMOUNT = "out_tax_amount";

    public static final String OUT_REASON = "out_reason";

    public static final String OUT_REMARK = "out_remark";

    public static final String OUT_DATE = "out_date";

    public static final String OUT_BY = "out_by";

    public static final String CONFIRM_USER = "confirm_user";

    public static final String MACHINECODE = "machinecode";

    public static final String UPDATE_INTERFACE = "update_interface";

    public static final String APPLY_RZLX = "apply_rzlx";

    public static final String APPLY_TAX_PERIOD = "apply_tax_period";

    public static final String COMMISSION_TYPE = "commission_type";

    public static final String COMMISSION_TAX = "commission_tax";

    public static final String COMMISSION_NAME = "commission_name";

    public static final String NEW_GF_TAXNO = "new_gf_taxno";

    public static final String TAX_RATE = "tax_rate";

    public static final String INVOICE_INFORMATION = "invoice_information";

    public static final String INVOICE_DEAL_STATUS = "invoice_deal_status";

    public static final String DXHY_MATCH_STATUS = "dxhy_match_status";

    public static final String SETTLEMENTAMOUNT = "settlementAmount";

    public static final String ACCOUNT_ADJUSTMENT_AMOUNT = "account_adjustment_amount";

    public static final String MATCHNO = "matchno";

    public static final String VENDERID = "venderid";

    public static final String VENDERTAXNO = "vendertaxno";

    public static final String DEDUCTIBLE_TAX = "deductible_tax";

    public static final String DEDUCTIBLE_TAX_RATE = "deductible_tax_rate";

    public static final String BBINDINGNO = "bbindingno";

    public static final String PACKINGNO = "packingno";

    public static final String REBATENO = "rebateno";

    public static final String REBATE_EXPRESSNO = "rebate_expressno";

    public static final String SCANNING_SERIANO = "scanning_seriano";

    public static final String PRINTCODE = "printcode";

    public static final String CERTIFICATE_NO = "certificate_no";

    public static final String INVOICE_SERIAL_NO = "invoice_serial_no";

    public static final String JVCODE = "jvcode";

    public static final String VENDERNAME = "vendername";

    public static final String REBATE_DATE = "rebate_date";

    public static final String MATCH_DATE = "match_date";

    public static final String HOST_STATUS = "host_status";

    public static final String REBATEYESORNO = "rebateyesorno";

    public static final String EXPRESSNOYESORNO = "expressnoyesorno";

    public static final String BBINDING_DATE = "bbinding_date";

    public static final String BINDYESORNO = "bindyesorno";

    public static final String PACKYESORNO = "packyesorno";

    public static final String RED_MONEY_AMOUNT = "red_money_amount";

    public static final String PAYMENT_AMOUNT = "payment_amount";

    public static final String COST_NO = "cost_no";

    public static final String RED_NOTICE_NUMBER = "red_notice_number";

    public static final String SCAN_MATCH_STATUS = "scan_match_status";

    public static final String BORROWYESORNO = "borrowyesorno";

    public static final String SCAN_MATCH_DATE = "scan_match_date";

    public static final String PACKING_DATE = "packing_date";

    public static final String BORROW_DATE = "borrow_date";

    public static final String APPLICATION_NUMBER = "application_number";

    public static final String COVERED_AMOUNT = "covered_amount";

    public static final String HOST_DATE = "host_date";

    public static final String FLOW_TYPE = "flow_type";

    public static final String JVNAME = "jvname";

    public static final String DOCUMENT_HEADER_TEXT = "document_header_text";

    public static final String DOCUMENT_TYPE = "document_type";

    public static final String WRITE_OFF = "write_off";

    public static final String CLEARANCE_VOUCHER = "clearance_voucher";

    public static final String CLEARING_DATE = "clearing_date";

    public static final String SHOW_CURRENCY_AMOUNT = "show_currency_amount";

    public static final String VOUCHER_CURRENCY = "voucher_currency";

    public static final String CURRENCY = "currency";

    public static final String POSTING_DATE = "posting_date";

    public static final String PAYMENT_DATE = "payment_date";

    public static final String TAX_CODE = "tax_code";

    public static final String FISCAL_YEAR = "fiscal_year";

    public static final String INVOICE_TEXT = "invoice_text";

    public static final String SUBJECT = "subject";

    public static final String COMPANY_CODE = "company_code";

    public static final String DK_INVOICEAMOUNT = "dk_invoiceAmount";

    public static final String TP_STATUS = "tp_status";

    public static final String MATCHING = "matching";

    public static final String SAP_DATE = "sap_date";

    public static final String PERIOD = "period";

    public static final String SHOP_NO = "shop_no";

    public static final String MATCHING_DATE = "matching_date";

    public static final String IS_DEL = "is_del";

    public static final String DEL_DATE = "del_date";

    public static final String BPMS_PAY_STATUS = "bpms_pay_status";

    public static final String COST_DEPT_ID = "cost_dept_id";

    public static final String NO_DEDUCTION = "no_deduction";

    public static final String FIXED_MATCHING = "fixed_matching";

    public static final String FIXED_MATCHING_DATE = "fixed_matching_date";

    public static final String FIXED_TAX_CODE = "fixed_tax_code";

    public static final String SUPPLIER_NUMBER = "supplier_number";

    public static final String PACKING_ADDRESS = "packing_address";

    public static final String SETTLEMENTNO = "settlementNo";

    public static final String CIPHER_TEXT = "cipher_text";

    public static final String GOODS_LIST_FLAG = "goods_list_flag";

    public static final String REMAINING_AMOUNT = "remaining_amount";

    public static final String HOST_TAXRATE = "host_taxRate";

    public static final String IS_GL = "is_gl";

    public static final String YXSE = "yxse";

    public static final String SCAN_FAIL_REASON = "scan_fail_reason";

    public static final String CONFIRM_REASON = "confirm_reason";

    public static final String BORROW_RETURN_DATE = "borrow_return_date";

    public static final String IS_RETURN_TICKET = "is_return_ticket";

    public static final String BORROW_DEPT = "borrow_dept";

    public static final String CATEGORY2 = "category2";

    public static final String YQKGXBZ = "yqkgxbz";

    public static final String BORROW_REASON = "borrow_reason";

    public static final String ID = "id";

    public static final String EPS_NO = "eps_no";

    public static final String CONFIRM_STATUS = "confirm_status";

    public static final String CONFIRM_USER_ID = "confirm_user_id";

    public static final String CATEGORY1 = "category1";

    public static final String XXLY = "xxly";

    public static final String CONFIRM_TIME = "confirm_time";

    public static final String ARIBA_CONFIRM_STATUS = "ariba_confirm_status";

    public static final String BORROW_RETURN_USER = "borrow_return_user";

    public static final String BORROW_USER = "borrow_user";

    public static final String GLZT = "glzt";

    public static final String SAP = "sap";

}
