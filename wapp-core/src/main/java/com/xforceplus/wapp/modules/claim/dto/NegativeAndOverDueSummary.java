package com.xforceplus.wapp.modules.claim.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-30 20:05
 **/
@Builder
public class NegativeAndOverDueSummary {
    @Getter
    @Setter
    private String negativeOverDueAmount;
}
