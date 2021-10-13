package com.xforceplus.wapp.modules.deduct.model;

import lombok.Data;

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
     * 税码
     */
    private String taxCode;

    /**
     * 供应商6D
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
}
