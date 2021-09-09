package com.xforceplus.wapp.modules.collect.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 统计发票各个税率的税额、金额实体
 * @author Colin.hu
 * @date 4/18/2018
 */
@Getter @Setter @ToString
public class RecordInvoiceStatistics extends AbstractBaseDomain {

    private static final long serialVersionUID = -541980853663321623L;

    private Double taxAmount;//税额
    private Double zkbl;//扣折比率
    private String invoiceNo;//发票号码
    private String jylx;//易交类型
    private Double taxRate;//税率
    private String invoiceCode;//发票代码
    private Double detailAmount;//金额
    private Double totalAmount;//价税合计
    private Double zkje;//折扣金额
    private String ywzk;//无有折扣
    private java.util.Date createDate;//创建时间
    private String depart;//门部

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
