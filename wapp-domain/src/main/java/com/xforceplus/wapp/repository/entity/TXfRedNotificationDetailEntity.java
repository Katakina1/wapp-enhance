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
    * 红字信息明细表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_red_notification_detail")
public class TXfRedNotificationDetailEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 红字信息表id
     */
    @TableField("apply_id")
    private Long applyId;

    /**
     * 税编版本号
     */
    @TableField("goods_no_ver")
    private String goodsNoVer;

    /**
     * 订单号
     */
    @TableField("detail_no")
    private String detailNo;

    /**
     * 劳务货物名称
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 税收分类编码
     */
    @TableField("goods_tax_no")
    private String goodsTaxNo;

    /**
     * 税编转换代码
     */
    @TableField("tax_convert_code")
    private String taxConvertCode;

    /**
     * 是否享受优惠政策
     */
    @TableField("tax_pre")
    private Integer taxPre;

    /**
     * 优惠内容
     */
    @TableField("tax_pre_con")
    private String taxPreCon;

    /**
     * 零税率标识
     */
    @TableField("zero_tax")
    private Integer zeroTax;

    /**
     * 规格型号
     */
    @TableField("model")
    private String model;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 数量
     */
    @TableField("num")
    private Double num;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private Double taxRate;

    /**
     * 单价
     */
    @TableField("unit_price")
    private Double unitPrice;

    /**
     * 不含税金额
     */
    @TableField("amount_without_tax")
    private Double amountWithoutTax;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private Double taxAmount;

    /**
     * 含税金额
     */
    @TableField("amount_with_tax")
    private Double amountWithTax;

    /**
     * 扣除额
     */
    @TableField("deduction")
    private Double deduction;

    /**
     * 创建时间
     */
    @TableField("create_date")
    private Date createDate;

    /**
     * 更新时间
     */
    @TableField("update_date")
    private Date updateDate;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;


    public static final String APPLY_ID = "apply_id";

    public static final String GOODS_NO_VER = "goods_no_ver";

    public static final String DETAIL_NO = "detail_no";

    public static final String GOODS_NAME = "goods_name";

    public static final String GOODS_TAX_NO = "goods_tax_no";

    public static final String TAX_CONVERT_CODE = "tax_convert_code";

    public static final String TAX_PRE = "tax_pre";

    public static final String TAX_PRE_CON = "tax_pre_con";

    public static final String ZERO_TAX = "zero_tax";

    public static final String MODEL = "model";

    public static final String UNIT = "unit";

    public static final String NUM = "num";

    public static final String TAX_RATE = "tax_rate";

    public static final String UNIT_PRICE = "unit_price";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String DEDUCTION = "deduction";

    public static final String CREATE_DATE = "create_date";

    public static final String UPDATE_DATE = "update_date";

    public static final String ID = "id";

}
