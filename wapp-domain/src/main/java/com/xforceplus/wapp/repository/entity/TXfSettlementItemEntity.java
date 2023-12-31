package com.xforceplus.wapp.repository.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import com.xforceplus.wapp.repository.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 结算单明细
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_settlement_item")
public class TXfSettlementItemEntity extends BaseEntity {

    static final long serialVersionUID = 1L;

    /**
     * 结算单编码
     */
    @TableField("settlement_no")
    private String settlementNo;
    
    /**
     * 业务单号，用来排序无实际用处
     */
    @TableField(exist = false)
    private String businessNo;

    /**
     * 明细编码
     */
    @TableField("salesbill_item_no")
    private String salesbillItemNo;

    /**
     * 明细代码
     */
    @TableField("item_code")
    private String itemCode;

    /**
     * 明细名称
     */
    @TableField("item_name")
    private String itemName;

    /**
     * 商品简称-税编简称
     */
    @TableField("item_short_name")
    private String itemShortName;

    /**
     * 规格型号
     */
    @TableField("item_spec")
    private String itemSpec;

    /**
     * 含税单价
     */
    @TableField("unit_price_with_tax")
    private BigDecimal unitPriceWithTax;

    /**
     * 单价
     */
    @TableField(value = "unit_price",updateStrategy= FieldStrategy.IGNORED,insertStrategy = FieldStrategy.IGNORED)
    private BigDecimal unitPrice;

    /**
     * 数量
     */
    @TableField(value = "quantity",updateStrategy= FieldStrategy.IGNORED,insertStrategy = FieldStrategy.IGNORED)
    private BigDecimal quantity;

    /**
     * 单位
     */
    @TableField("quantity_unit")
    private String quantityUnit;

    /**
     * 含税金额
     */
    @TableField("amount_with_tax")
    private BigDecimal amountWithTax;

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
     * 税率 目前整数存储，需要程序单独处理
1---1%
9---9%

     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 是否享受税收优惠政策 0 - 不 1- 是
     */
    @TableField("tax_pre")
    private String taxPre;

    /**
     * 优惠政策内容
     */
    @TableField("tax_pre_con")
    private String taxPreCon;

    /**
     * 零税率标志 空 - 非0税率，0-出口退税，1-免税，2-不征税，3-普通0税率
     */
    @TableField("zero_tax")
    private String zeroTax;

    /**
     * 税收分类编码
     */
    @TableField("goods_tax_no")
    private String goodsTaxNo;

    /**
     * 编码版本号
     */
    @TableField("goods_no_ver")
    private String goodsNoVer;

    /**
     * 已开票 未开票 
     */
    @TableField("item_status")
    private Integer itemStatus;

    /**
     * 0 正常 1 待匹配税编 2 待确认金额
     */
    @TableField("item_flag")
    private Integer itemFlag;

    /**
     * 第三方id  索赔明细Id、蓝票明细ID
     */
    @TableField("thrid_id")
    private Long thridId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("update_user")
    private Long updateUser;

    @TableField("create_time")
    private Date createTime;

    @TableField("remark")
    private String remark;

    @TableField("create_user")
    private Long createUser;

    /**
     * 是否成品油
     */
    @TableField("is_oil")
    private Integer isOil;

    /**
     * 业务单明细关系ID
     */
    @TableField("item_ref_id")
    private Long itemRefId;

    public static final String SETTLEMENT_NO = "settlement_no";

    public static final String SALESBILL_ITEM_NO = "salesbill_item_no";

    public static final String ITEM_CODE = "item_code";

    public static final String ITEM_NAME = "item_name";

    public static final String ITEM_SHORT_NAME = "item_short_name";

    public static final String ITEM_SPEC = "item_spec";

    public static final String UNIT_PRICE_WITH_TAX = "unit_price_with_tax";

    public static final String UNIT_PRICE = "unit_price";

    public static final String QUANTITY = "quantity";

    public static final String QUANTITY_UNIT = "quantity_unit";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String IS_OIL = "is_oil";

    public static final String TAX_RATE = "tax_rate";

    public static final String TAX_PRE = "tax_pre";

    public static final String TAX_PRE_CON = "tax_pre_con";

    public static final String ZERO_TAX = "zero_tax";

    public static final String GOODS_TAX_NO = "goods_tax_no";

    public static final String GOODS_NO_VER = "goods_no_ver";

    public static final String ITEM_STATUS = "item_status";

    public static final String ITEM_FLAG = "item_flag";

    public static final String THRID_ID = "thrid_id";

    public static final String UPDATE_TIME = "update_time";

    public static final String ID = "id";

    public static final String UPDATE_USER = "update_user";

    public static final String CREATE_TIME = "create_time";

    public static final String REMARK = "remark";

    public static final String CREATE_USER = "create_user";

    public static final String ITEM_REF_ID="item_ref_id";

}
