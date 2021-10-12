package com.xforceplus.wapp.modules.deduct.service;

import lombok.Data;

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
     * 税码
     */
    private String taxCode;

    /**
     * 协议类型编码
     */
    private String reasonCode;
}
