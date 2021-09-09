package com.xforceplus.wapp.modules.certification.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionExcelInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;

/**
 * 查询认证业务层接口
 * @author Colin.hu
 * @date 4/13/2018
 */
public interface CertificationQueryService {

    /**
     * 获取查询认证分页数据对象
     * @param map 参数
     * @return 分页对象
     */
    PagedQueryResult<InvoiceCollectionInfo> queryCertification(Map<String, Object> map);
    List<InvoiceCollectionExcelInfo> queryCertificationExport(Map<String, Object> map);
    ReportStatisticsEntity getCertificationListCount(Map<String, Object> map);
}
