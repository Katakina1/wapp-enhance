package com.xforceplus.wapp.repository.entity;

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
 * @since 2021-10-12
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
     * 门店编码
     */
    @TableField("store_nbr")
    private String storeNbr;

    /**
     * 部门编码
     */
    @TableField("dept_nbr")
    private String deptNbr;

    /**
     * 供应商编码
     */
    @TableField("supplier_code")
    private String supplierCode;

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
    private Double price;

    /**
     * 单位
     */
    @TableField("unit")
    private Double unit;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private Double taxRate;

    /**
     * 项目数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * vnpk成本
     */
    @TableField("vnpk_cost")
    private Double vnpkCost;

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
    private Double remainingAmount;

    @TableField("create_date")
    private Date createDate;

    @TableField("update_date")
    private Date updateDate;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    public static final String VERDICT_DATE = "verdict_date";

    public static final String STORE_NBR = "store_nbr";

    public static final String DEPT_NBR = "dept_nbr";

    public static final String SUPPLIER_CODE = "supplier_code";

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

    public static final String CREATE_DATE = "create_date";

    public static final String UPDATE_DATE = "update_date";

    public static final String ID = "id";

}
