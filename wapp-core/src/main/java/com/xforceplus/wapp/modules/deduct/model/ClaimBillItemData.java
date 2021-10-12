package com.xforceplus.wapp.modules.deduct.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 类描述：接收数据清洗索赔单明细数据结构
 *
 * @ClassName AgreementBillData
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 10:26
 */
@Data
public class ClaimBillItemData {
    /**
     *定案日期
     */
    private String verdictDate;
    /**
     *门店编码
     */
    private String storeNbr;
    /**
     *部门编码
     */
    private String deptNbr;
    /**
     *供应商编码
     */
    private String supplierCode;
    /**
     *中文品名
     */
    private String cnDesc;
    /**
     *商品编码
     */
    private String itemNo;
    /**
     *商品条码
     */
    private String upc;
    /**
     *单价
     */
    private BigDecimal price;
    /**
     *单位
     */
    private BigDecimal unit;
    /**
     *税率
     */
    private BigDecimal taxRate;
    /**
     *项目数量
     */
    private BigDecimal quantity;
    /**
     *vnpk成本
     */
    private BigDecimal vnpkCost;
    /**
     *vnpk数量
     */
    private BigDecimal vnpkQuantity;
    /**
     *类别编码
     */
    private String gategoryNbr;
    /**
     *剩余额度
     */
    private BigDecimal remainingAmount;
}
