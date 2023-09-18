package com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.send;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 缴税账单查询接口
 * @Author: ChenHang
 * @Date: 2023/6/27 16:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendQueryTaxBill implements Serializable {

    /**
     * 税单号
     */
    private String taxDocNo;

}
