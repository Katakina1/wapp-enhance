package com.xforceplus.wapp.modules.einvoice.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created on 2018/04/19.
 * @author marvin
 * 底账表明细实体类
 */
@Getter
@Setter
@ToString
public class RecordInvoiceDetail extends AbstractBaseDomain {

    private static final long serialVersionUID = 227596895014450330L;

    /**
     *  发票代码 + 发票号码
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

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
