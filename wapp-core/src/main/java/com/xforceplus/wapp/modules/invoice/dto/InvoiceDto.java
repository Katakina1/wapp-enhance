package com.xforceplus.wapp.modules.invoice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class InvoiceDto {
    /**
     * id
     */
    private Long id;

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
    private String paperDrewDate;

    /**
     * 购方名称
     */
    private String purchaserName;

    /**
     * 购方纳税人识别号
     */
    private String purchaserTaxNo;

    /**
     * 购方地址电话
     */
    private String purchaserAddrTel;

    /**
     * 购方银行名称与账号合并
     */
    private String purchaserBankNameAccount;

    /**
     * 校验码
     */
    private String checkCode;

    /**
     * 机器编码
     */
    private String machineCode;

    /**
     * 密文
     */
    private String cipherText;

    /**
     * 含税金额
     */
    private BigDecimal amountWithTax;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 不含税金额
     */
    private BigDecimal amountWithoutTax;

    /**
     * 销方名称
     */
    private String sellerName;

    /**
     * 销方纳税人识别号
     */
    private String sellerTaxNo;

    /**
     * 销方地址电话
     */
    private String sellerAddrTel;

    /**
     * 销方银行名称账号
     */
    private String sellerBankNameAccount;

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
     * 发票备注
     */

    private String remark;

    /**
     * 税率
     */

    private String taxRate;

    /**
     * 购方no
     */

    private String purchaserNo;

    /**
     * 销方no
     */

    private String sellerNo;

    /**
     * 发票状态 0-作废,1-正常,2-红冲,3-失控,4-异常,9-未知
     */

    private String status;

    /**
     * 红蓝标识 1-蓝字发票 2-红字发票
     */

    private String invoiceColor;

    /**
     * 红冲状态 1-未红冲（蓝票）2-部分红冲 3-红冲
     */

    private String redFlag;

    /**
     * 税务大类：01 增值税专用 02 增值税普通 03 其他 04 进出口类
     */

    private String taxCategory;

    /**
     * 行业开具类型：10 增值税（常规）20 通行费 21 火车票22 行程单23 客运汽车24 出租车25 过路费30 成品油40 海关缴款通知书50 农产品收购60 机动车61 二手车70 其他发票
     */

    private String industryIssueType;

    /**
     * 发票介质 01 纸票（常规）02 纸票（卷票）03 纸票（定额）04 纸票（通用机打）05 纸票（其他）06电子（常规）07电子（区块链）
     */

    private String invoiceMedium;

    /**
     * 认证状态：0-待认证1-已认证
     */

    private String authStatus;

    /**
     * 认证后状态 1-已抵扣2-认证异常3-已转出
     */

    private String authAfterStatus;

    /**
     * 底账勾选状态:1-未勾选 2-已勾选 3-已勾选（签名确认）4-不可勾选
     */

    private String authSyncStatus;

    /**
     * 认证所属期
     */

    private String authTaxPeriod;

    /**
     * 抵扣用途(0-默认1-抵扣 2-不抵扣)
     */

    private String authUse;

    /**
     * 认证业务日期
     */

    private String authBussiDate;

    /**
     * 有效税额
     */

    private BigDecimal effectiveTaxAmount;

    /**
     * 剩余可匹配的额度
     */

    private BigDecimal remainingAmount;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */

    private Date updateTime;


    List<InvoiceItemDto> details ;
}
