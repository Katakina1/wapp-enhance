package com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 入账回参实体
 * @Author: ChenHang
 * @Date: 2023/7/26 17:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntryAccountResultDTO implements Serializable {

    /**
     * 系统来源
     * S001:BMS非商结算
     * S002:增值税海关缴款书
     */
    private String businessSource;
    /**
     * 发票号码
     */
    private String invoiceNo;

    /***
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 海关票号码
     */
    private String taxDocNo;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 错误信息
     */
    private String msg;

}
