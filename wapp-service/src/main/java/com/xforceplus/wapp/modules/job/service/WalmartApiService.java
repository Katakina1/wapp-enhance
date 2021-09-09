package com.xforceplus.wapp.modules.job.service;

import com.xforceplus.wapp.modules.job.entity.WalmartApiEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface WalmartApiService {



    /**
     * 查询购方名称
     */
    List<WalmartApiEntity> searchGf();
}
