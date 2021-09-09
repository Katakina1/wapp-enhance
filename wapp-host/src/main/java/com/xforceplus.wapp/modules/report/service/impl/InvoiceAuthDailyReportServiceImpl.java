package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.InvoiceAuthDailyReportDao;
import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthDailyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InvoiceAuthDailyReportServiceImpl implements InvoiceAuthDailyReportService {

    @Autowired
    private InvoiceAuthDailyReportDao invoiceAuthDailyReportDao;

    @Override
    public List<DailyReportEntity> getList(String schemaLabel,Map<String, Object> map) {
        return invoiceAuthDailyReportDao.getList(schemaLabel,map);
    }

    @Override
    public String getCurrentTaxPeriod(String schemaLabel,String taxno) {
        return invoiceAuthDailyReportDao.getCurrentTaxPeriod(schemaLabel,taxno);
    }
}
