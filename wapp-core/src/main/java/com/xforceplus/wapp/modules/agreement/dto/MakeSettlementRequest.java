package com.xforceplus.wapp.modules.agreement.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-21 19:54
 **/
@Setter
@Getter
public class MakeSettlementRequest {
    private List<Long> ids;

    private List<String> billNos;
}
