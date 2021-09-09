package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionExcelInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionResultExcelInfo;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/12/14 22:56
 */
public interface AuthenticationResultQueryService {


     PagedQueryResult<InvoiceCollectionInfo> queryCertification(Map<String,Object> map);

    List<InvoiceCollectionResultExcelInfo> queryCertificationForExport(Map<String,Object> params);
    ReportStatisticsEntity getCertificationListCount(Map<String,Object> map);
}
