package com.xforceplus.wapp.modules.backFill.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel("发票详情实体")
@Data
public class RecordInvoiceDetailsModel {
    private Long id;

    /**
     * 唯一标识(发票代码+发票号码)
     */
    private String uuid;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 明细序号
     */
    private String detailNo;

    /**
     * 货物或应税劳务名称
     */
    private String goodsName;

    /**
     * 规格型号
     */
    private String model;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数量
     */
    private String num;

    /**
     * 单价
     */
    private String unitPrice;

    /**
     * 金额
     */
    private String detailAmount;

    /**
     * 税率
     */
    private String taxRate;

    /**
     * 税额
     */
    private String taxAmount;

    /**
     * 车牌号
     */
    private String cph;

    /**
     * 类型
     */
    private String lx;

    /**
     * 通行日期起
     */
    private String txrqq;

    /**
     * 通行日期止
     */
    private String txrqz;

    /**
     * 商品编码
     */
    private String goodsNum;

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
     * 红冲序列号
     */
    private String redticketDataSerialNumber;

    /**
     * 红冲税额
     */
    private BigDecimal redRushTaxAmount;

    private String category1;
}