package com.xforceplus.wapp.modules.transferOut.entity;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:16:46
 * 发票明细表
*/

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.util.Date;

@Getter @Setter @ToString
public class DetailEntity extends AbstractBaseDomain {

    private Long id;
    private String uuid;//唯一标识(发票代码+发票号码)
    private String invoiceCode;//发票代码
    private String invoiceNo;//发票号码
    private String detailNo;//明细序号
    private String goodsName;//货物或应税劳务名称
    private String model;//规格型号
    private String unit;//单位
    private String num;//数量
    private String unitPrice;//单价
    private String detailAmount;//金额
    private String taxRate;//税率
    private String taxAmount;//税额
    private String cph;//车牌号
    private String lx;//类型
    private String txrqq;//通行日期起
    private String txrqz;//通行日期止
    private String goodsNum;//商品编码





    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
