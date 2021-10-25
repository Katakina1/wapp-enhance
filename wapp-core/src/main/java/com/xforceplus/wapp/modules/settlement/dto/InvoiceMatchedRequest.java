package com.xforceplus.wapp.modules.settlement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-22 14:27
 **/
@Setter
@Getter
@ApiModel
public class InvoiceMatchedRequest {
    @ApiModelProperty("发票")
    private List<Invoice> invoiceList;

    @Setter
    @Getter
    public static class Invoice{
        String invoiceNo;
        String invoiceCode;
    }
}
