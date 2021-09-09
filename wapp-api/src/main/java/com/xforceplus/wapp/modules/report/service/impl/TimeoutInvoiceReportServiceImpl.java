package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.TimeoutInvoiceReportDao;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.TimeoutInvoiceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TimeoutInvoiceReportServiceImpl implements TimeoutInvoiceReportService {
    @Autowired
    private TimeoutInvoiceReportDao timeoutInvoiceReportDao;

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return timeoutInvoiceReportDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return timeoutInvoiceReportDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryListAll(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return timeoutInvoiceReportDao.queryListAll(schemaLabel,map);
    }
}
