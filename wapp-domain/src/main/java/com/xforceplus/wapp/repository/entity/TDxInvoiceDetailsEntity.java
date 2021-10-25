package com.xforceplus.wapp.repository.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 发票明细表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_dx_Invoice_details")
public class TDxInvoiceDetailsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 货物名称
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 规格型号
     */
    @TableField("goods_model")
    private String goodsModel;

    /**
     * 单价
     */
    @TableField("goods_price")
    private BigDecimal goodsPrice;

    /**
     * 单位
     */
    @TableField("goods_unit")
    private String goodsUnit;

    /**
     * 数量
     */
    @TableField("goods_number")
    private Integer goodsNumber;

    /**
     * 金额
     */
    @TableField("goods_amount")
    private BigDecimal goodsAmount;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 红冲数量
     */
    @TableField("red_rush_number")
    private Integer redRushNumber;

    /**
     * 红冲金额
     */
    @TableField("red_rush_amount")
    private BigDecimal redRushAmount;

    /**
     * 红冲单价
     */
    @TableField("red_rush_price")
    private BigDecimal redRushPrice;

    /**
     * 红票序列号
     */
    @TableField("redticket_data_serial_number")
    private String redticketDataSerialNumber;

    @TableField("id")
    private Long id;


    public static final String GOODS_NAME = "goods_name";

    public static final String GOODS_MODEL = "goods_model";

    public static final String GOODS_PRICE = "goods_price";

    public static final String GOODS_UNIT = "goods_unit";

    public static final String GOODS_NUMBER = "goods_number";

    public static final String GOODS_AMOUNT = "goods_amount";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String TAX_RATE = "tax_rate";

    public static final String RED_RUSH_NUMBER = "red_rush_number";

    public static final String RED_RUSH_AMOUNT = "red_rush_amount";

    public static final String RED_RUSH_PRICE = "red_rush_price";

    public static final String REDTICKET_DATA_SERIAL_NUMBER = "redticket_data_serial_number";

    public static final String ID = "id";

}
