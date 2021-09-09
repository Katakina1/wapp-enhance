package com.xforceplus.wapp.modules.collect.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 未补发票明细
 * @author Colin.hu
 * @date 4/12/2018
 */
@Getter @Setter @ToString
public class InvoiceDetailInfo extends AbstractBaseDomain {

    private static final long serialVersionUID = 2463915817574186083L;

    private String taxAmount;//税额
    private String goodsName;//货物或应税劳务名称
    private String invoiceNo;//发票号码
    private String num;//数量
    private String detailNo;//明细序号
    private String unitPrice;//单价
    private String lx;//类型
    private String uuid;//唯一标识(发票代码+发票号码)
    private String txrqz;//通行日期止
    private String taxRate;//税率
    private String invoiceCode;//发票代码
    private String unit;//单位
    private String detailAmount;//金额
    private String txrqq;//通行日期起
    private String goodsNum;//商品编码
    private String model;//规格型号
    private String cph;//车牌号

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
