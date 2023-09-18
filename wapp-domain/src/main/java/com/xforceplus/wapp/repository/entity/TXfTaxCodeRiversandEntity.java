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
    * 
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-09-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_tax_code_riversand")
public class TXfTaxCodeRiversandEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("tax_pre_con")
    private String taxPreCon;

    @TableField("item_spec")
    private String itemSpec;

    @TableField("item_code")
    private String itemCode;

    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    @TableId(type= IdType.INPUT,value="item_no")
    private String itemNo;

    @TableField("goods_tax_no")
    private String goodsTaxNo;

    @TableField("quantity_unit")
    private String quantityUnit;

    @TableField("item_name")
    private String itemName;

    @TableField("status")
    private String status;//0-默认 1-比对一致 2-比对不一致 -1-上传失败 3-上传3.0平台成功 4-上传已经存在

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user")
    private Long updateUser;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tax_rate")
    private BigDecimal taxRate;


    @TableField("zero_tax")
    private String zeroTax;

    @TableField("create_user")
    private Long createUser;


    @TableField("delete_flag")
    private String deleteFlag;

    @TableField("tax_pre")
    private String taxPre;



    public static final String TAX_PRE_CON = "tax_pre_con";

    public static final String ITEM_SPEC = "item_spec";

    public static final String ITEM_CODE = "item_code";

    public static final String UPDATE_TIME = "update_time";

    public static final String ITEM_NO = "item_no";

    public static final String GOODS_TAX_NO = "goods_tax_no";

    public static final String QUANTITY_UNIT = "quantity_unit";

    public static final String ITEM_NAME = "item_name";

    public static final String STATUS = "status";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String ID = "id";

    public static final String TAX_RATE = "tax_rate";

    public static final String ZERO_TAX = "zero_tax";

    public static final String CREATE_USER = "create_user";

    public static final String DELETE_FLAG = "delete_flag";

    public static final String TAX_PRE = "tax_pre";



}
