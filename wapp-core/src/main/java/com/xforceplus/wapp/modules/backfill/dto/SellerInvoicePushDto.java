package com.xforceplus.wapp.modules.backfill.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SellerInvoicePushDto implements Serializable {

    private TXfSellerInvoiceEntity sellerInvoiceMain;

    private List<TXfSellerInvoiceItemEntity> sellerInvoiceDetails;
}
