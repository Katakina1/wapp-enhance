package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * <p>
    * 底账明细表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_dx_record_invoice_detail")
public class TDxRecordInvoiceDetailEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识(发票代码+发票号码)
     */
    @TableField("uuid")
    private String uuid;

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
     * 明细序号
     */
    @TableField("detail_no")
    private String detailNo;

    /**
     * 货物或应税劳务名称
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 规格型号
     */
    @TableField("model")
    private String model;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 数量
     */
    @TableField(value = "num",updateStrategy= FieldStrategy.IGNORED,insertStrategy = FieldStrategy.IGNORED)
    private String num;

    /**
     * 单价
     */
    @TableField(value = "unit_price",updateStrategy= FieldStrategy.IGNORED,insertStrategy = FieldStrategy.IGNORED)
    private String unitPrice;

    /**
     * 金额
     */
    @TableField("detail_amount")
    private String detailAmount;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private String taxRate;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private String taxAmount;

    /**
     * 车牌号
     */
    @TableField("cph")
    private String cph;

    /**
     * 类型
     */
    @TableField("lx")
    private String lx;

    /**
     * 通行日期起
     */
    @TableField("txrqq")
    private String txrqq;

    /**
     * 通行日期止
     */
    @TableField("txrqz")
    private String txrqz;

    /**
     * 商品编码
     */
    @TableField("goods_num")
    private String goodsNum;

    /**
     * 红冲数量
     */
    @TableField("red_rush_number")
    private Integer redRushNumber;

    /**
     * 红冲金额
     */
    @TableField("red_rush_amount")
    private BigDecimal redRushAmount;

    /**
     * 红冲单价
     */
    @TableField("red_rush_price")
    private BigDecimal redRushPrice;

    /**
     * 红冲序列号
     */
    @TableField("redticket_data_serial_number")
    private String redticketDataSerialNumber;

    /**
     * 红冲税额
     */
    @TableField("red_rush_tax_amount")
    private BigDecimal redRushTaxAmount;

    @TableField("category1")
    private String category1;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * （业务字段）折扣金额 2023-06-15新增
     */
    @TableField(exist = false)
    private BigDecimal discountDetailAmount;
    /**
     * （业务字段）被占用的不含税金额 2023-06-15新增
     */
    @TableField(exist = false)
    private BigDecimal useAmountWithoutTax;
    /**
     * （业务字段）被占用的含税金额 2023-06-15新增
     */
    @TableField(exist = false)
    private BigDecimal useAmountWithTax;
    /**
     * （业务字段）被占用的数量 2023-06-15新增
     */
    @TableField(exist = false)
    private BigDecimal useQuantity;
    /**
     * （业务字段）协议被占用的不含税金额 2023-06-15新增
     */
    @TableField(exist = false)
    private BigDecimal agreementUseAmountWithoutTax;
    /**
     * （业务字段）协议被占用的含税金额 2023-06-15新增
     */
    @TableField(exist = false)
    private BigDecimal agreementUseAmountWithTax;
    /**
     * （业务字段）协议被占用的数量 2023-06-15新增
     */
    @TableField(exist = false)
    private BigDecimal agreementUseQuantity;
//    /**
//     * 税收分类编码
//     */
//    @TableField("goods_tax_no")
//    private String goodsTaxNo;

    public static final String UUID = "uuid";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String INVOICE_NO = "invoice_no";

    public static final String DETAIL_NO = "detail_no";

    public static final String GOODS_NAME = "goods_name";

    public static final String MODEL = "model";

    public static final String UNIT = "unit";

    public static final String NUM = "num";

    public static final String UNIT_PRICE = "unit_price";

    public static final String DETAIL_AMOUNT = "detail_amount";

    public static final String TAX_RATE = "tax_rate";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String CPH = "cph";

    public static final String LX = "lx";

    public static final String TXRQQ = "txrqq";

    public static final String TXRQZ = "txrqz";

    public static final String GOODS_NUM = "goods_num";

    public static final String RED_RUSH_NUMBER = "red_rush_number";

    public static final String RED_RUSH_AMOUNT = "red_rush_amount";

    public static final String RED_RUSH_PRICE = "red_rush_price";

    public static final String REDTICKET_DATA_SERIAL_NUMBER = "redticket_data_serial_number";

    public static final String RED_RUSH_TAX_AMOUNT = "red_rush_tax_amount";

    public static final String CATEGORY1 = "category1";

    public static final String ID = "id";

}
