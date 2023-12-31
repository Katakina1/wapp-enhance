package com.xforceplus.wapp.modules.deduct.model;

import lombok.Data;

import java.util.Date;

/**
 * 类描述：接收数据清洗协议单数据结构
 *
 * @ClassName AgreementBillData
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 10:26
 */
@Data
public class AgreementBillData extends  DeductBillBaseData {
    /**
     * 文档类型
     */
    private String documentType;
    /**
     * 文档编码
     */
    private String documentNo;

    /**
     * 协议类型编码
     */
    private String reasonCode;
    /**
     * 供应商sap编号
     */
    private String memo;
    /**
     * 税码
     */
    private String taxCode;
    /**
     * 协议号
     */
    private String reference;
    /**
     * 协议类型
     */
    private String referenceType;
    /**
     * 入账日期
     */
    private Date postingDate;

}
