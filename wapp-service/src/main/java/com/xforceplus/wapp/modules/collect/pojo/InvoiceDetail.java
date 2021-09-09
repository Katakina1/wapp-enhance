package com.xforceplus.wapp.modules.collect.pojo;

import com.xforceplus.wapp.modules.job.pojo.BasePojo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 查验发票响应明细
 * @author Colin.hu
 * @date 4/17/2018
 */
@Getter @Setter @ToString
public class InvoiceDetail extends BasePojo {

    private static final long serialVersionUID = -5224647604590979343L;

    /**
     * 明细编号
     */
    private String detailNo;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 规格型号
     */
    private String specificationModel;

    /**
     * 计量单位
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
     * 费用项目
     */
    private String costItem;

    /**
     * 费用金额
     */
    private String costAmount;

    /**
     * 类型
     */
    private String lx;

    /**
     * 车牌号
     */
    private String cph;

    /**
     * 通行日期起
     */
    private String txrqq;

    /**
     * 通行日期至
     */
    private String txrqz;
}
