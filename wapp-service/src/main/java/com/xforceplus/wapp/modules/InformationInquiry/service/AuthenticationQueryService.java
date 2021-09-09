package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.collect.entity.AribaInvoiceCollectionTaxExcelInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionTaxExcelInfo;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/12/14 22:56
 */
public interface AuthenticationQueryService {


     PagedQueryResult<InvoiceCollectionInfo> queryCertification(Map<String, Object> map);
     PagedQueryResult<InvoiceCollectionInfo> queryAribaCertification(Map<String, Object> map);
     List<InvoiceCollectionTaxExcelInfo> queryCertificationForExcel(Map<String, Object> map);
     List<InvoiceCollectionTaxExcelInfo> queryAribaCertificationForExcel(Map<String, Object> map);

     ReportStatisticsEntity getCertificationListCount(Map<String, Object> map);
     ReportStatisticsEntity getAribaCertificationListCount(Map<String, Object> map);
}
