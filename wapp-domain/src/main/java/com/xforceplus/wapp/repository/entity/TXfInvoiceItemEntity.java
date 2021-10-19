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
    * 发票明细
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_invoice_item")
public class TXfInvoiceItemEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 发票代码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 发票号码
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 发票ID
     */
    @TableField("invoice_id")
    private String invoiceId;

    /**
     * 货物或应税劳务代码
     */
    @TableField("cargo_code")
    private String cargoCode;

    /**
     * 货物或应税劳务名称
     */
    @TableField("cargo_name")
    private String cargoName;

    /**
     * 规格型号
     */
    @TableField("item_spec")
    private String itemSpec;

    /**
     * 商品单位
     */
    @TableField("quantity_unit")
    private String quantityUnit;

    /**
     * 商品数量
     */
    @TableField("quantity")
    private BigDecimal quantity;

    /**
     * 发票号码
     */
    @TableField("tax_rate")
    private String taxRate;

    /**
     * 不含税单价
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

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
     * 含税金额
     */
    @TableField("amount_with_tax")
    private BigDecimal amountWithTax;

    /**
     * 税收分类编码
     */
    @TableField("goods_tax_no")
    private String goodsTaxNo;

    /**
     * 车牌号
     */
    @TableField("plate_number")
    private String plateNumber;

    /**
     * 车辆类型
     */
    @TableField("vehicle_type")
    private String vehicleType;

    /**
     * 通行日期起
     */
    @TableField("toll_start_date")
    private String tollStartDate;

    /**
     * 通行日期止
     */
    @TableField("toll_end_date")
    private String tollEndDate;

    /**
     * 创建日期
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新日期
     */
    @TableField(value="update_time", update="now(3)" )
    private Date updateTime;

    @TableField("id")
    private Long id;


    public static final String INVOICE_CODE = "invoice_code";

    public static final String INVOICE_NO = "invoice_no";

    public static final String INVOICE_ID = "invoice_id";

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

    public static final String GOODS_TAX_NO = "goods_tax_no";

    public static final String PLATE_NUMBER = "plate_number";

    public static final String VEHICLE_TYPE = "vehicle_type";

    public static final String TOLL_START_DATE = "toll_start_date";

    public static final String TOLL_END_DATE = "toll_end_date";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String ID = "id";

}
