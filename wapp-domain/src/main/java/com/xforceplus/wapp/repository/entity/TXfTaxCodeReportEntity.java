package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
/**
 * <p>
    * 
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-09-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_tax_code_report")
public class TXfTaxCodeReportEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("item_spec")
    private String itemSpec;

    @TableField("tax_pre_con")
    private String taxPreCon;

    @TableField("item_name")
    private String itemName;

    @TableField("rs_zero_tax")
    private String rsZeroTax;

    @TableField("report_desc")
    private String reportDesc;

    @TableField("tax_rate")
    private BigDecimal taxRate;

    @TableField("update_user")
    private Long updateUser;

    @TableField("rs_tax_pre_con")
    private String rsTaxPreCon;


    @TableField("tax_pre")
    private String taxPre;

    @TableField("quantity_unit")
    private String quantityUnit;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("create_user")
    private Long createUser;

    @TableField("rs_tax_pre")
    private String rsTaxPre;

    @TableField(value="update_time", update="getdate()", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("delete_flag")
    private String deleteFlag;

    @TableField("item_code")
    private String itemCode;

    @TableField("rs_tax_rate")
    private BigDecimal rsTaxRate;

    @TableField("goods_tax_no")
    private String goodsTaxNo;


    @TableField("zero_tax")
    private String zeroTax;

    @TableField("create_time")
    private Date createTime;

    @TableField("rs_goods_tax_no")
    private String rsGoodsTaxNo;


    @TableField("item_no")
    private String itemNo;

    @TableField("status")
    private String status;


    @TableField("dispose_status")
    private String disposeStatus;

    @TableField("dispose_time")
    private Date disposeTime;


    public static final String ITEM_SPEC = "item_spec";

    public static final String TAX_PRE_CON = "tax_pre_con";

    public static final String ITEM_NAME = "item_name";

    public static final String RS_ZERO_TAX = "rs_zero_tax";

    public static final String REPORT_DESC = "report_desc";

    public static final String TAX_RATE = "tax_rate";

    public static final String UPDATE_USER = "update_user";

    public static final String RS_TAX_PRE_CON = "rs_tax_pre_con";

    public static final String TAX_PRE = "tax_pre";

    public static final String QUANTITY_UNIT = "quantity_unit";

    public static final String ID = "id";

    public static final String CREATE_USER = "create_user";

    public static final String RS_TAX_PRE = "rs_tax_pre";

    public static final String UPDATE_TIME = "update_time";

    public static final String DELETE_FLAG = "delete_flag";

    public static final String ITEM_CODE = "item_code";

    public static final String RS_TAX_RATE = "rs_tax_rate";

    public static final String GOODS_TAX_NO = "goods_tax_no";

    public static final String ZERO_TAX = "zero_tax";

    public static final String CREATE_TIME = "create_time";

    public static final String RS_GOODS_TAX_NO = "rs_goods_tax_no";

    public static final String ITEM_NO = "item_no";

    public static final String STATUS = "status";

    public static final String DISPOSE_STATUS = "dispose_status";

    public static final String DISPOSE_TIME = "dispose_time";

}
