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
    * 换票表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_invoice_exchange")
public class TXfInvoiceExchangeEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 底账发票id
     */
    @TableField("invoice_id")
    private Long invoiceId;

    /**
     * 状态 0待换票 1已上传 2已完成 9删除
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * orgcode
     */
    @TableField("jvcode")
    private String jvcode;

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
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 退单号
     */
    @TableField("return_no")
    private String returnNo;

    /**
     * 快递公司
     */
    @TableField("express_company")
    private String expressCompany;

    /**
     * 快递单号
     */
    @TableField("waybill_no")
    private String waybillNo;

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
    @TableField("paper_drew_date")
    private String paperDrewDate;

    /**
     * 新开发票id，逗号隔开
     */
    @TableField("new_invoice_id")
    private String newInvoiceId;

    /**
     * 发票类型
     */
    @TableField("invoice_type")
    private String invoiceType;

    /**
     * 供应商号
     */
    @TableField("venderid")
    private String venderid;

    /**
     * 凭证号
     */
    @TableField("voucher_no")
    private String voucherNo;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


    public static final String ID = "id";

    public static final String INVOICE_ID = "invoice_id";

    public static final String STATUS = "status";

    public static final String CREATE_TIME = "create_time";

    public static final String JVCODE = "jvcode";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String BUSINESS_TYPE = "business_type";

    public static final String RETURN_NO = "return_no";

    public static final String EXPRESS_COMPANY = "express_company";

    public static final String WAYBILL_NO = "waybill_no";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String INVOICE_NO = "invoice_no";

    public static final String PAPER_DREW_DATE = "paper_drew_date";

    public static final String NEW_INVOICE_ID = "new_invoice_id";

    public static final String INVOICE_TYPE = "invoice_type";

    public static final String VENDERID = "venderid";

    public static final String VOUCHER_NO = "voucher_no";

    public static final String REMARK = "remark";

}
