package com.xforceplus.wapp.modules.settlement.dto;

import lombok.Data;

/**
 * 结算单撤销红字信息表
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:05
 **/
@Data
public class SettlementUndoRedNotificationRequest {
    private Long settlementId;

    private String revertRemark;

}
