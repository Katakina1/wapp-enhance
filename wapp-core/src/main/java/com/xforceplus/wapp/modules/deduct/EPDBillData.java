package com.xforceplus.wapp.modules.deduct;

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
}
