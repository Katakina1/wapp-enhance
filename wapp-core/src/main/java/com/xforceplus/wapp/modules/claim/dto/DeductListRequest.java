package com.xforceplus.wapp.modules.claim.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 16:44
 **/
@Setter
@Getter
public class DeductListRequest {
    private String purchaserNo;
    private String billNo;
    private long page = 1;
    private long size = 50;
}
