package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="t_dx_record_invoice")
public class RecordInvoiceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
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
     * 开票日期
     */
    private Date invoiceDate;

    /**
     * 购方税号
     */
    private String gfTaxNo;

    /**
     * 购方名称
     */
    private String gfName;

    /**
     * 购方地址电话
     */
    private String gfAddressAndPhone;

    /**
     * 购方开户行及账号
     */
    private String gfBankAndNo;

    /**
     * 销方税号
     */
    private String xfTaxNo;

    /**
     * 销方名称
     */
    private String xfName;

    /**
     * 销方地址及电话
     */
    private String xfAddressAndPhone;

    /**
     * 销方开户行及账号
     */
    private String xfBankAndNo;

    /**
     * 金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 价格合计
     */
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
     */
    private String invoiceStatus;

    /**
     * 发票状态修改时间
     */
    private Date statusUpdateDate;

    /**
     * 发票状态最后修改时间
     */
    private Date lastUpdateDate;

    /**
     * 认证时间
     */
    private Date rzhDate;

    /**
     * 签收时间
     */
    private Date qsDate;

    /**
     * 最晚认证归属期 yyyyMM
     */
    private String rzhBelongDateLate;

    /**
     * 实际认证归属期 yyyyMM
     */
    private String rzhBelongDate;

    /**
     * 认证确认时间
     */
    private Date confirmDate;

    /**
     * 认证方式 1-勾选认证 2-扫描认证
     */
    private String rzhType;

    /**
     * 是否认证 0-未认证 1-已认证
     */
    private String rzhYesorno;

    /**
     * 提交认证操作人账号
     */
    private String gxUserAccount;

    /**
     * 提交认证操作人
     */
    private String gxUserName;

    /**
     * 底账来源  0-采集 1-查验 2-录入
     */
    private String sourceSystem;

    /**
     * 是否有效 1-有效 0-无效
     */
    private String valid;

    /**
     * 发票代码+发票号码    唯一索引 防重复
     */
    private String uuid;

    /**
     * 采集时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 勾选时间
     */
    private Date gxDate;

    /**
     * 认证结果失败错误码
     */
    private String rzhBackCode;

    /**
     * 认证结果回传信息
     */
    private String rzhBackMsg;

    /**
     * 当前税款所属期
     */
    private String dqskssq;

    /**
     * 当前税款所属期勾选截止日
     */
    private String gxjzr;

    /**
     * 当前税款所属期可勾选发票开票日期范围起
     */
    private String gxfwq;

    /**
     * 当前税款所属期可勾选发票开票日期范围止
     */
    private String gxfwz;

    /**
     * 是否已勾选
     */
    private String sfygx;

    /**
     * 是否存入明细 0 无明细 1 有明细
     */
    private String detailYesorno;

    /**
     * 勾选方式(0-手工勾选 1-扫码勾选 2-导入勾选 3- 智能勾选 4-手工认证 5-扫码认证 6-导入认证)
     */
    private String gxType;

    /**
     * 认证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
     */
    private String authStatus;

    /**
     * 发送认证时间
     */
    private Date sendDate;

    /**
     * 认证类型(1-抵扣 2-退税 3-代理退税   发票已认证、已勾选有该标签）
     */
    private String rzlx;

    /**
     * 是否代办退税(0：否 1：是)
     */
    private String sfdbts;

    /**
     * 签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收5-pdf上传签收
     */
    private String qsType;

    /**
     * 签收结果（0-未签收 1-已签收）
     */
    private String qsStatus;

    /**
     * 校验码
     */
    private String checkCode;

    /**
     * 通行费标志(Y-可抵扣通行费，N-不可抵扣通行费)
     */
    private String txfbz;

    /**
     * 零税率标志 (空:非零税率，1:税率栏位显示“免税”，2:税率栏位显示“不征收”，3:零税率
     */
    private String lslbz;

    /**
     * 转出状态 0-未转出   1-全部转出  2-部分转出
     */
    private String outStatus;

    /**
     * 转出金额
     */
    private BigDecimal outInvoiceAmout;

    /**
     * 转出税额
     */
    private BigDecimal outTaxAmount;

    /**
     * 转出原因1-免税项目用 ；2-集体福利,个人消费；3-非正常损失；4-简易计税方法征税项目用；5-免抵退税办法不得抵扣的进项税额；6-纳税检查调减进项税额；7-红字专用发票通知单注明的进项税额；8-上期留抵税额抵减欠税
     */
    private String outReason;

    /**
     * 转出备注
     */
    private String outRemark;

    /**
     * 转出日期
     */
    private Date outDate;

    /**
     * 转出人
     */
    private String outBy;

    /**
     * 确认人
     */
    private String confirmUser;

    /**
     * 机器编号
     */
    private String machinecode;

    /**
     * 发票状态更新字段-- 用于接口
     */
    private Date updateInterface;

    /**
     * 申请的认证类型
     */
    private String applyRzlx;

    /**
     * 申请认证税款所属期
     */
    private String applyTaxPeriod;

    /**
     * 代开标志
     */
    private String commissionType;

    /**
     * 代开税号
     */
    private String commissionTax;

    /**
     * 代开企业名称
     */
    private String commissionName;

    /**
     * 采集税号
     */
    private String newGfTaxno;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 发票信息
     */
    private String invoiceInformation;

    /**
     * 发票处理状态
     */
    private String invoiceDealStatus;

    /**
     * 大象平台匹配状态
     */
    private String dxhyMatchStatus;

    /**
     * 结算金额
     */
    private BigDecimal settlementamount;

    /**
     * 帐务调整金额(发票金额-结算金额)
     */
    private BigDecimal accountAdjustmentAmount;

    /**
     * 匹配关联号
     */
    private String matchno;

    /**
     * 供应商号
     */
    private String venderid;

    /**
     * 供应商税号
     */
    private String vendertaxno;

    /**
     * 抵扣税额（0税率普票专用）
     */
    private BigDecimal deductibleTax;

    /**
     * 抵扣税率（0税率普票专用）
     */
    private BigDecimal deductibleTaxRate;

    /**
     * 装订号
     */
    private String bbindingno;

    /**
     * 装箱号
     */
    private String packingno;

    /**
     * 退单号
     */
    private String rebateno;

    /**
     * 退单快递号
     */
    private String rebateExpressno;

    /**
     * 扫描流水号
     */
    private String scanningSeriano;

    /**
     * 打印号
     */
    private String printcode;

    /**
     * 凭证号
     */
    private String certificateNo;

    /**
     * 扫描流水号
     */
    private String invoiceSerialNo;

    /**
     * orgcode
     */
    private String jvcode;

    /**
     * 供应商名称
     */
    private String vendername;

    /**
     * 生成退单日期
     */
    private Date rebateDate;

    /**
     * 匹配日期
     */
    private Date matchDate;

    /**
     * host状态（0，1：loaded;13:invoice delete;14 invoice reactived;10:unmatched;12matched out;11:matched equal;15:reconciled(manual);19:reconciled(auto);9:extracted for payment;99:paid;999:purged;
     */
    private String hostStatus;

    /**
     * 是否生成退单号（0-未生成，1-已生成）
     */
    private String rebateyesorno;

    /**
     * 是否录入邮包号（0-未录入，1-已录入）
     */
    private String expressnoyesorno;

    /**
     * 装订日期
     */
    private Date bbindingDate;

    /**
     * 是否s生成装订号（0-未生成，1-已生成）
     */
    private String bindyesorno;

    /**
     * 是否录入装箱号（0-未录入，1-已录入）
     */
    private String packyesorno;

    /**
     * 可红冲金额
     */
    private BigDecimal redMoneyAmount;

    /**
     * 付款金额
     */
    private Long paymentAmount;

    /**
     * 费用号
     */
    private String costNo;

    /**
     * 红字通知单号
     */
    private String redNoticeNumber;

    /**
     * 扫描匹配状态（0-未匹配，1-匹配成功，2匹配失败）
     */
    private String scanMatchStatus;

    /**
     * 是否借阅(0-未借阅，1-已被借阅)
     */
    private String borrowyesorno;

    /**
     * 扫描匹配日期
     */
    private Date scanMatchDate;

    /**
     * 装箱日期
     */
    private Date packingDate;

    /**
     * 借阅日期
     */
    private Date borrowDate;

    /**
     * 申请编号 yyyyMM+4位流水号
     */
    private String applicationNumber;

    /**
     * 已冲销金额
     */
    private BigDecimal coveredAmount;

    /**
     * HOST回写时间
     */
    private Date hostDate;

    /**
     * 发票流程类型（1：商品，2：费用）
     */
    private String flowType;

    private BigDecimal hostTaxrate;

    /**
     * orgname
     */
    private String jvname;

    /**
     * 凭证抬头文本
     */
    private String documentHeaderText;

    /**
     * 凭证类型
     */
    private String documentType;

    /**
     * 冲销清账
     */
    private String writeOff;

    /**
     * 清账凭证
     */
    private String clearanceVoucher;

    /**
     * 清账日期
     */
    private Date clearingDate;

    /**
     * 显示货币的金额
     */
    private BigDecimal showCurrencyAmount;

    /**
     * 凭证货币
     */
    private String voucherCurrency;

    /**
     * 本币
     */
    private String currency;

    /**
     * 过账日期
     */
    private Date postingDate;

    /**
     * 付款日期
     */
    private Date paymentDate;

    /**
     * 税码
     */
    private String taxCode;

    /**
     * 会计年度
     */
    private String fiscalYear;

    /**
     * 文本
     */
    private String invoiceText;

    /**
     * 科目
     */
    private String subject;

    /**
     * 公司代码
     */
    private String companyCode;

    /**
     * 抵扣发票金额
     */
    private BigDecimal dkInvoiceamount;

    /**
     * 退票状态（0：初始，1：待退票，2：已退票）
     */
    private String tpStatus;

    private String confirmStatus;

    private String confirmReason;

    private Date confirmTime;

    private Long confirmUserId;

    /**
     * 租金0 代表未匹配，1代表匹配成功
     */
    private String matching;

    private String sap;

    /**
     * sap时间
     */
    private Date sapDate;

    /**
     * 期间
     */
    private String period;

    /**
     * 店号
     */
    private String shopNo;

    /**
     * 匹配时间
     */
    private Date matchingDate;

    private String scanFailReason;

    /**
     * 是否删除 0-未删除 1--已删除
     */
    private String isDel;

    /**
     * 删除时间
     */
    private Date delDate;

    /**
     * BPMS付款状态 0--未付款 1-已付款
     */
    private String bpmsPayStatus;

    /**
     * 成本中心号
     */
    private String costDeptId;

    private String epsNo;

    private BigDecimal yxse;

    private String xxly;

    private String yqkgxbz;

    private String glzt;

    /**
     * 1不进手工认证
     */
    private String noDeduction;

    private String isReturnTicket;

    /**
     * 租赁匹配是否成功：0未成功1成功
     */
    private String fixedMatching;

    /**
     * 租赁匹配日期
     */
    private Date fixedMatchingDate;

    /**
     * 租赁taxcode
     */
    private String fixedTaxCode;

    private String aribaConfirmStatus;

    /**
     * 10位供应商号
     */
    private String supplierNumber;

    /**
     * 装箱存放地址
     */
    private String packingAddress;

    private String borrowUser;

    private String borrowDept;

    private Date borrowReturnDate;

    private String borrowReturnUser;

    private String borrowReason;

    private String isGl;

    private String category1;

    private String category2;
}