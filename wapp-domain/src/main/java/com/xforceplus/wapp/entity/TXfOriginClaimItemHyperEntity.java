package com.xforceplus.wapp.entity;

import com.xforceplus.wapp.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 索赔单Hyper明细
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_origin_claim_item_hyper")
public class TXfOriginClaimItemHyperEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id 关联t_xf_bill_job主键
     */
    @TableField("job_id")
    private Integer jobId;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField(value="update_time", update="now(3)" )
    private Date updateTime;

    @TableField("upc_nbr")
    private String upcNbr;

    @TableField("unit_cost")
    private String unitCost;

    @TableField("vendor_stock_id")
    private String vendorStockId;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("claim_nbr")
    private String claimNbr;

    @TableField("vndr_nbr")
    private String vndrNbr;

    @TableField("dept_nbr")
    private String deptNbr;

    @TableField("tax_rate")
    private String taxRate;

    @TableField("final_date")
    private String finalDate;

    @TableField("category_nbr")
    private String categoryNbr;

    @TableField("vnpk_cost")
    private String vnpkCost;

    @TableField("item_qty")
    private String itemQty;

    @TableField("line_cost")
    private String lineCost;

    @TableField("vnpk_qty")
    private String vnpkQty;

    @TableField("cn_desc")
    private String cnDesc;

    @TableField("item_nbr")
    private String itemNbr;


    public static final String JOB_ID = "job_id";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPC_NBR = "upc_nbr";

    public static final String UNIT_COST = "unit_cost";

    public static final String VENDOR_STOCK_ID = "vendor_stock_id";

    public static final String ID = "id";

    public static final String CLAIM_NBR = "claim_nbr";

    public static final String VNDR_NBR = "vndr_nbr";

    public static final String DEPT_NBR = "dept_nbr";

    public static final String TAX_RATE = "tax_rate";

    public static final String FINAL_DATE = "final_date";

    public static final String CATEGORY_NBR = "category_nbr";

    public static final String VNPK_COST = "vnpk_cost";

    public static final String ITEM_QTY = "item_qty";

    public static final String LINE_COST = "line_cost";

    public static final String VNPK_QTY = "vnpk_qty";

    public static final String CN_DESC = "cn_desc";

    public static final String ITEM_NBR = "item_nbr";

}
