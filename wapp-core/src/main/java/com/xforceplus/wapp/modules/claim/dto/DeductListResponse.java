package com.xforceplus.wapp.modules.claim.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 19:27
 **/
@Setter
@Getter
public class DeductListResponse {
    /**
     * 已上传发票数量
     */
    private Integer invoiceCount;
    /**
     * 业务单据编号
     */
    private String billNo;

    /**
     * 业务单据类型;1:索赔;2:协议;3:EPD
     */
    private Integer businessType;

    /**
     * 关联结算单编码
     */
    private String refSettlementNo;

    /**
     * 定案、入账日期
     */
    private Date verdictDate;

    /**
     * 扣款日期
     */
    private Date deductDate;

    /**
     * 所扣发票
     */
    private String deductInvoice;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 协议类型编码
     */
    private String agreementReasonCode;

    /**
     * 协议号
     */
    private String agreementReference;

    /**
     * 协议税码
     */
    private String agreementTaxCode;

    /**
     * 协议供应商6D
     */
    private String agreementMemo;

    /**
     * 协议凭证号码
     */
    private String agreementDocumentNumber;

    /**
     * 协议凭证类型
     */
    private String agreementDocumentType;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 业务单状态
     * 索赔单:101待匹配明细;102待确认税编;103待确认税差;104待匹配蓝票;105:待匹配结算单;106已匹配结算单;107待审核;108已撤销
     * 协议单:201待匹配结算单;202已匹配结算单;203已锁定;204已取消
     * EPD单:301待匹配结算单;302已匹配结算单
     */
    private Integer status;

    /**
     * 扣款公司jv_code
     */
    private String purchaserNo;

    /**
     * 供应商编码
     */
    private String sellerNo;

    /**
     * 供应商名称
     */
    private String sellerName;

    /**
     * 不含税金额
     */
    private BigDecimal amountWithoutTax;

    /**
     * 含税金额
     */
    private BigDecimal amountWithTax;

    private Long id;


    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 0 未超期，1超期
     */
    private Integer overdue;

}
