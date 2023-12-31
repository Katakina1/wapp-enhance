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
    * 业务单据明细信息
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-12-21
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
    private BigDecimal price;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

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

    /**
     * 上传批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 来源id 唯一索引
     */
    @TableField("source_id")
    private Long sourceId;

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
     * 索赔单号
     */
    @TableField("claim_no")
    private String claimNo;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("create_time")
    private Date createTime;

    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    /**
     * 规格型号
     */
    @TableField("item_spec")
    private String itemSpec;


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

    public static final String BATCH_NO = "batch_no";

    public static final String SOURCE_ID = "source_id";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String CLAIM_NO = "claim_no";

    public static final String ID = "id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String ITEM_SPEC = "item_spec";

}
