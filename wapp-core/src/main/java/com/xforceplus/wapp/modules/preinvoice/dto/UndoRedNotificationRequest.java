package com.xforceplus.wapp.modules.preinvoice.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-21 14:45
 **/
@Setter
@Getter
public class UndoRedNotificationRequest {
    private String invoiceNo;
    private String invoiceCode;
    private String remark;
}
