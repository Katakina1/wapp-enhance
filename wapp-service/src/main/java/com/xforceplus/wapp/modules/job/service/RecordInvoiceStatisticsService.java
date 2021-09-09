package com.xforceplus.wapp.modules.job.service;

import com.xforceplus.wapp.modules.job.pojo.InvoiceDetailInfo;

import java.util.List;

public interface RecordInvoiceStatisticsService {
      void countTaxRate(List<InvoiceDetailInfo> recordInvoiceDetail,
                               String invoiceCode, String invoiceNo,String linkName);
}
