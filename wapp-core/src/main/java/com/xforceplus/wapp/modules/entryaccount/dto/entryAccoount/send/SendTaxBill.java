package com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.send;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 发送缴税账单匹配结果
 * @Author: ChenHang
 * @Date: 2023/6/27 15:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendTaxBill implements Serializable {

    /**
     * 账单ID
     */
    private String id;
    /**
     * 匹配状态
     */
    private String matchState;
    /**
     * 税单号
     */
    private String taxDocNo;

}
