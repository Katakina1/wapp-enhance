package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * <p>
    * 
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_pre_invoice_item")
public class TXfPreInvoiceItemEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 预制发票id
     */
    @TableField("pre_invoice_id")
    private Long preInvoiceId;

    /**
     * 税收分类编码
     */
    @TableField("goods_tax_no")
    private String goodsTaxNo;

    /**
     * 货物或应税劳务名称
     */
    @TableField("cargo_name")
    private String cargoName;

    /**
     * 货物或应税劳务代码
     */
    @TableField("cargo_code")
    private String cargoCode;

    /**
     * 规格型号
     */
    @TableField("item_spec")
    private String itemSpec;

    /**
     * 不含税单价
     */
    @TableField("unit_price")
    private Double unitPrice;

    /**
     * 数量
     */
    @TableField("quantity")
    private Double quantity;

    /**
     * 单位

     */
    @TableField("quantity_unit")
    private String quantityUnit;

    /**
     * 税率 目前整数存储，需要程序单独处理
1---1%
9---9%

     */
    @TableField("tax_rate")
    private Double taxRate;

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
     * 含税金额
     */
    @TableField("amount_with_tax")
    private BigDecimal amountWithTax;

    /**
     * 编码版本号
     */
    @TableField("goods_no_ver")
    private String goodsNoVer;

    /**
     * 是否享受税收优惠政策 0 否 1 是
     */
    @TableField("tax_pre")
    private String taxPre;

    /**
     * 是否享受税收优惠政策内容
     */
    @TableField("tax_pre_con")
    private String taxPreCon;

    /**
     * 零税率标志 空 - 非0税率，0-出口退税，1-免税，2-不征税，3-普通0税率
     */
    @TableField("zero_tax")
    private String zeroTax;

    /**
     * 是否打印单价数量 0 否 1 是
     */
    @TableField("print_content_flag")
    private String printContentFlag;

    /**
     * 分类码
     */
    @TableField("item_type_code")
    private String itemTypeCode;

    @TableField("price_method")
    private String priceMethod;

    @TableField("id")
    private Long id;


    public static final String PRE_INVOICE_ID = "pre_invoice_id";

    public static final String GOODS_TAX_NO = "goods_tax_no";

    public static final String CARGO_NAME = "cargo_name";

    public static final String CARGO_CODE = "cargo_code";

    public static final String ITEM_SPEC = "item_spec";

    public static final String UNIT_PRICE = "unit_price";

    public static final String QUANTITY = "quantity";

    public static final String QUANTITY_UNIT = "quantity_unit";

    public static final String TAX_RATE = "tax_rate";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String GOODS_NO_VER = "goods_no_ver";

    public static final String TAX_PRE = "tax_pre";

    public static final String TAX_PRE_CON = "tax_pre_con";

    public static final String ZERO_TAX = "zero_tax";

    public static final String PRINT_CONTENT_FLAG = "print_content_flag";

    public static final String ITEM_TYPE_CODE = "item_type_code";

    public static final String PRICE_METHOD = "price_method";

    public static final String ID = "id";

}
