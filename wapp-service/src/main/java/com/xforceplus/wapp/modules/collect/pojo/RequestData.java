package com.xforceplus.wapp.modules.collect.pojo;

import com.xforceplus.wapp.modules.job.pojo.BasePojo;
import lombok.Getter;
import lombok.Setter;

/**
 * 手动校验请求参数实体
 * @author Colin.hu
 * @date 4/16/2018
 */
@Getter @Setter
public class RequestData extends BasePojo {

    private static final long serialVersionUID = -8657872830194078957L;
    /**
     * 购方税号
     */
    private String buyerTaxNo;

    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 开票时间
     */
    private String invoiceDate;

    /**
     * 校验吗
     */
    private String checkCode;

    /**
     * 金额
     */
    private String invoiceAmount;
}
