package com.xforceplus.wapp.modules.deduct.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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
    private Date verdictDate;
    /**
     *门店编码 对应购方编码
     */
    private String storeNbr;
    /**
     *部门编码
     */
    private String deptNbr;
    /**
     *供应商编码
     */
    private String sellerNo;
    /**
     *中文品名
     */
    private String cnDesc;
    /**
     *商品编码
     */
    private String itemNo;
    /**
     * 规格型号转换代码，查询用户系统接口获取商品信息
     */
    private String itemNbr;

    /**
     * 规格型号
     */
    private String itemSpec;
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
    private String unit;
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
    private String categoryNbr;
    /**
     *不含税金额
     */
    private BigDecimal amountWithoutTax;
    /**
     * 资源id
     */
    private Long id;
    /**
     * 索赔单编号
     */
    private String claimNo;
}
