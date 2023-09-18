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
    * 发票明细表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-11-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_seller_invoice_item")
public class TXfSellerInvoiceItemEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 发票序列号
     */
    private String invoiceId;

    /**
     * 预制发票序列号
     */
    private String preInvoiceId;

    /**
     * 预制发票明细号
     */
    private String preInvoiceItemId;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票号码
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 货物或应税劳务代码
     */
    private String cargoCode;

    /**
     * 货物或应税劳务名称
     */
    private String cargoName;

    /**
     * 规格型号
     */
    private String itemSpec;

    /**
     * 数量单位
     */
    private String quantityUnit;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 不含税单价
     */
    private BigDecimal unitPrice;

    /**
     * 不含税金额
     */
    private BigDecimal amountWithoutTax;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 含税金额
     */
    private BigDecimal amountWithTax;

    /**
     * 不含税折扣金额
     */
    private BigDecimal discountWithoutTax;

    /**
     * 折扣税额
     */
    private BigDecimal discountTax;

    /**
     * 含税折扣金额
     */
    private BigDecimal discountWithTax;

    /**
     * 折扣率
     */
    private BigDecimal discountRate;

    /**
     * 商品税目
     */
    private String taxItem;

    /**
     * 编码版本号
     */
    private String goodsNoVer;

    /**
     * 税收分类编码
     */
    private String goodsTaxNo;

    /**
     * 是否享受税收优惠政策0-不1-享受
     */
    private String taxPre;

    /**
     * 享受税收优惠政策内容
     */
    private String taxPreCon;

    /**
     * 零税率标志空-非0税率；0-出口退税1-免税2-不征税3-普通0税率
     */
    private String zeroTax;

    /**
     * 扣除额
     */
    private BigDecimal taxDedunction;

    /**
     * 折扣行标志
     */
    private String discountFlag;

    /**
     * 价格方式
     */
    private String priceMethod;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 创建操作账号
     */
    private String createUserId;

    /**
     * 更新账号
     */
    private String updateUserId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 是否打印单价数量（0：打印，1：不打印）
     */
    private String isPrintContentFlag;

    /**
     * 订单行号(企业)
     */
    private String orderDetailNo;


    public static final String ID = "id";

    public static final String INVOICE_ID = "invoice_id";

    public static final String PRE_INVOICE_ID = "pre_invoice_id";

    public static final String PRE_INVOICE_ITEM_ID = "pre_invoice_item_id";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String INVOICE_NO = "invoice_no";

    public static final String CARGO_CODE = "cargo_code";

    public static final String CARGO_NAME = "cargo_name";

    public static final String ITEM_SPEC = "item_spec";

    public static final String QUANTITY_UNIT = "quantity_unit";

    public static final String QUANTITY = "quantity";

    public static final String TAX_RATE = "tax_rate";

    public static final String UNIT_PRICE = "unit_price";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String DISCOUNT_WITHOUT_TAX = "discount_without_tax";

    public static final String DISCOUNT_TAX = "discount_tax";

    public static final String DISCOUNT_WITH_TAX = "discount_with_tax";

    public static final String DISCOUNT_RATE = "discount_rate";

    public static final String TAX_ITEM = "tax_item";

    public static final String GOODS_NO_VER = "goods_no_ver";

    public static final String GOODS_TAX_NO = "goods_tax_no";

    public static final String TAX_PRE = "tax_pre";

    public static final String TAX_PRE_CON = "tax_pre_con";

    public static final String ZERO_TAX = "zero_tax";

    public static final String TAX_DEDUNCTION = "tax_dedunction";

    public static final String DISCOUNT_FLAG = "discount_flag";

    public static final String PRICE_METHOD = "price_method";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String CREATE_USER_ID = "create_user_id";

    public static final String UPDATE_USER_ID = "update_user_id";

    public static final String ORDER_NO = "order_no";

    public static final String IS_PRINT_CONTENT_FLAG = "is_print_content_flag";

    public static final String ORDER_DETAIL_NO = "order_detail_no";

}
