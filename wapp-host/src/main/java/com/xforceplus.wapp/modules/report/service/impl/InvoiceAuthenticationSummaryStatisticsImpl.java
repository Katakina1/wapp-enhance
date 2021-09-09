package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.InvoiceAuthenticationSummaryStatisticsDao;
import com.xforceplus.wapp.modules.report.entity.InvoiceAuthenticationStatisticEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthenticationSummaryStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/13
 * 认证发票汇总报表业务层
 */
@Service
public class InvoiceAuthenticationSummaryStatisticsImpl implements InvoiceAuthenticationSummaryStatisticsService{

    @Autowired
    private InvoiceAuthenticationSummaryStatisticsDao invoiceAuthenticationSummaryStatisticsDao;
    @Override
    public List<InvoiceAuthenticationStatisticEntity> queryList(Map<String, Object> map,String schemaLabel) {
        return invoiceAuthenticationSummaryStatisticsDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(Map<String, Object> map,String schemaLabel) {
        return invoiceAuthenticationSummaryStatisticsDao.queryTotalResult(map,schemaLabel);
    }

}
