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
    * 索赔单Sams明细
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_origin_claim_item_sams")
public class TXfOriginClaimItemSamsEntity extends BaseEntity {

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

    @TableField("primary_desc")
    private String primaryDesc;

    @TableField("item_tax_pct")
    private String itemTaxPct;

    @TableField("ship_cost")
    private String shipCost;

    @TableField("item_nbr")
    private String itemNbr;

    @TableField("rtn_date")
    private String rtnDate;

    @TableField("ship_retail")
    private String shipRetail;

    @TableField("dept_nbr")
    private String deptNbr;

    @TableField("claim_number")
    private String claimNumber;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("vendor_number")
    private String vendorNumber;

    @TableField("store_nbr")
    private String storeNbr;

    @TableField("unit")
    private String unit;

    @TableField("vendor_tax_id_chc")
    private String vendorTaxIdChc;

    @TableField("vendor_name")
    private String vendorName;

    @TableField("vendor_tax_id_jv")
    private String vendorTaxIdJv;

    @TableField("report_code")
    private String reportCode;

    @TableField("ship_qty")
    private String shipQty;

    @TableField("old_item")
    private String oldItem;


    public static final String JOB_ID = "job_id";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String PRIMARY_DESC = "primary_desc";

    public static final String ITEM_TAX_PCT = "item_tax_pct";

    public static final String SHIP_COST = "ship_cost";

    public static final String ITEM_NBR = "item_nbr";

    public static final String RTN_DATE = "rtn_date";

    public static final String SHIP_RETAIL = "ship_retail";

    public static final String DEPT_NBR = "dept_nbr";

    public static final String CLAIM_NUMBER = "claim_number";

    public static final String ID = "id";

    public static final String VENDOR_NUMBER = "vendor_number";

    public static final String STORE_NBR = "store_nbr";

    public static final String UNIT = "unit";

    public static final String VENDOR_TAX_ID_CHC = "vendor_tax_id_chc";

    public static final String VENDOR_NAME = "vendor_name";

    public static final String VENDOR_TAX_ID_JV = "vendor_tax_id_jv";

    public static final String REPORT_CODE = "report_code";

    public static final String SHIP_QTY = "ship_qty";

    public static final String OLD_ITEM = "old_item";

}
