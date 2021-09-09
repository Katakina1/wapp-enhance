package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.ComprehensiveInvoiceQueryDao;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ComprehensiveInvoiceQueryServiceImpl implements ComprehensiveInvoiceQueryService {

    @Autowired
    private ComprehensiveInvoiceQueryDao comprehensiveInvoiceQueryDao;

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return comprehensiveInvoiceQueryDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return comprehensiveInvoiceQueryDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryListAll(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return comprehensiveInvoiceQueryDao.queryListAll(schemaLabel,map);
    }

    @Override
    public List<OptionEntity> searchGf(String schemaLabel,Long userId) {
        return comprehensiveInvoiceQueryDao.searchGf(schemaLabel,userId);
    }

    @Override
    public List<String> searchXf(String schemaLabel,Map<String, Object> map) {
        return comprehensiveInvoiceQueryDao.searchXf(schemaLabel,map);
    }
}
