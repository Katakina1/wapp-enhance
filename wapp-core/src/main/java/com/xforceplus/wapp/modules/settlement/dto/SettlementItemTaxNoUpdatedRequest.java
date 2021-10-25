package com.xforceplus.wapp.modules.settlement.dto;

import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-23 19:20
 **/
@Setter
@Getter
public class SettlementItemTaxNoUpdatedRequest {
    private List<TXfSettlementItemEntity> item;

}
