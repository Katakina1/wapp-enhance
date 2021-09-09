package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.ExceptionalInvoiceReportDao;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.ExceptionalInvoiceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExceptionalInvoiceReportServiceImpl implements ExceptionalInvoiceReportService {
    @Autowired
    private ExceptionalInvoiceReportDao exceptionalInvoiceReportDao;

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return exceptionalInvoiceReportDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return exceptionalInvoiceReportDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryListAll(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return exceptionalInvoiceReportDao.queryListAll(schemaLabel,map);
    }
}
