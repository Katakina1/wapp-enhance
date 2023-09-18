package com.xforceplus.wapp.modules.deduct.model;

import lombok.Data;

import java.util.Date;

/**
 * 类描述：接收数据清洗EPD单数据结构
 *
 * @ClassName AgreementBillData
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 10:26
 */
@Data
public class EPDBillData extends DeductBillBaseData {
    /**
     * 文档类型
     */
    private String documentType;
    /**
     * 文档编码
     */
    private String documentNo;
    /**
     * 税码
     */
    private String taxCode;

    /**
     * 供应商sap编号
     */
    private String memo;

    /**
     *协议类型编码
     */
    private String reasonCode;
    /**
     * 协议号
     */
    private String reference;

    /**
     * 入账日期
     */
    private Date postingDate;
    /**
     *付款日期
     */
    private String paymentDate;
    /**
     * 所扣发票
     */
    private String invoiceReference;

}
