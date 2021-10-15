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
    * 业务单据明细信息
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_bill_deduct_item")
public class TXfBillDeductItemEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 定案日期
     */
    @TableField("verdict_date")
    private Date verdictDate;

    /**
     * 门店编码,对应购方编码
     */
    @TableField("purchaser_no")
    private String purchaserNo;

    /**
     * 部门编码
     */
    @TableField("dept_nbr")
    private String deptNbr;

    /**
     * 供应商编码 对应销方编码
     */
    @TableField("seller_no")
    private String sellerNo;

    /**
     * 中文品名
     */
    @TableField("cn_desc")
    private String cnDesc;

    /**
     * 商品编码
     */
    @TableField("item_no")
    private String itemNo;

    /**
     * 商品条码
     */
    @TableField("upc")
    private String upc;

    /**
     * 单价
     */
    @TableField("price")
    private String price;

    /**
     * 单位
     */
    @TableField("unit")
    private BigDecimal unit;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 项目数量
     */
    @TableField("quantity")
    private BigDecimal quantity;

    /**
     * vnpk成本
     */
    @TableField("vnpk_cost")
    private BigDecimal vnpkCost;

    /**
     * vnpk数量
     */
    @TableField("vnpk_quantity")
    private Integer vnpkQuantity;

    /**
     * 类别编码
     */
    @TableField("gategory_nbr")
    private String gategoryNbr;

    /**
     * 剩余额度
     */
    @TableField("remaining_amount")
    private BigDecimal remainingAmount;

    /**
     * 税收分类编码
     */
    @TableField("goods_tax_no")
    private String goodsTaxNo;

    /**
     * 是否享受税收优惠政策 0 否 1 是
     */
    @TableField("tax_pre")
    private String taxPre;

    /**
     * 优惠政策内容 
     */
    @TableField("tax_pre_con")
    private String taxPreCon;

    /**
     * 零税率
     */
    @TableField("zero_tax")
    private String zeroTax;

    /**
     * 税编简称
     */
    @TableField("item_short_name")
    private String itemShortName;

    /**
     * 税编版本
     */
    @TableField("goods_no_ver")
    private String goodsNoVer;

    /**
     * 不含税金额
     */
    @TableField("amount_without_tax")
    private BigDecimal amountWithoutTax;

    @TableField("create_date")
    private Date createDate;

    @TableField("update_date")
    private Date updateDate;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    public static final String VERDICT_DATE = "verdict_date";

    public static final String PURCHASER_NO = "purchaser_no";

    public static final String DEPT_NBR = "dept_nbr";

    public static final String SELLER_NO = "seller_no";

    public static final String CN_DESC = "cn_desc";

    public static final String ITEM_NO = "item_no";

    public static final String UPC = "upc";

    public static final String PRICE = "price";

    public static final String UNIT = "unit";

    public static final String TAX_RATE = "tax_rate";

    public static final String QUANTITY = "quantity";

    public static final String VNPK_COST = "vnpk_cost";

    public static final String VNPK_QUANTITY = "vnpk_quantity";

    public static final String GATEGORY_NBR = "gategory_nbr";

    public static final String REMAINING_AMOUNT = "remaining_amount";

    public static final String GOODS_TAX_NO = "goods_tax_no";

    public static final String TAX_PRE = "tax_pre";

    public static final String TAX_PRE_CON = "tax_pre_con";

    public static final String ZERO_TAX = "zero_tax";

    public static final String ITEM_SHORT_NAME = "item_short_name";

    public static final String GOODS_NO_VER = "goods_no_ver";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String CREATE_DATE = "create_date";

    public static final String UPDATE_DATE = "update_date";

    public static final String ID = "id";

}
