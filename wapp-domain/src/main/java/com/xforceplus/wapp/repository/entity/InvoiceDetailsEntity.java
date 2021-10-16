package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * t_dx_Invoice_details
 * @author 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="t_dx_Invoice_details")
public class InvoiceDetailsEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 货物名称
     */
    private String goodsName;

    /**
     * 规格型号
     */
    private String goodsModel;

    /**
     * 单价
     */
    private BigDecimal goodsPrice;

    /**
     * 单位
     */
    private String goodsUnit;

    /**
     * 数量
     */
    private Integer goodsNumber;

    /**
     * 金额
     */
    private BigDecimal goodsAmount;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 红冲数量
     */
    private Integer redRushNumber;

    /**
     * 红冲金额
     */
    private BigDecimal redRushAmount;

    /**
     * 红冲单价
     */
    private BigDecimal redRushPrice;

    /**
     * 红票序列号
     */
    private String redticketDataSerialNumber;
}