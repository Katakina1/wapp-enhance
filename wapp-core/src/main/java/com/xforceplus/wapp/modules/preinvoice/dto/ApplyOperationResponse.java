package com.xforceplus.wapp.modules.preinvoice.dto;

import lombok.Data;

import java.util.List;

/**
 * 返回待重新拆票的预制发票明细
 */
@Data
public class ApplyOperationResponse {
    int code ;
    String message;
    List<PreInvoiceItem> details ;
}
