package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.InputTaxReportDao;
import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import com.xforceplus.wapp.modules.report.service.InputTaxReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InputTaxReportServiceImpl implements InputTaxReportService {

    @Autowired
    InputTaxReportDao inputTaxReportDao;

    @Override
    public Double getTotalAmount(String schemaLabel,Map<String, Object> map) {
        return inputTaxReportDao.getTotalAmount(schemaLabel,map);
    }

    @Override
    public Double getTotalTax(String schemaLabel,Map<String, Object> map) {
        return inputTaxReportDao.getTotalTax(schemaLabel,map);
    }

    @Override
    public Double getTotalOutTax(String schemaLabel,Map<String, Object> map) {
        return inputTaxReportDao.getTotalOutTax(schemaLabel,map);
    }

    @Override
    public List<DailyReportEntity> getOutTaxDetail(String schemaLabel, Map<String, Object> map) {
        return inputTaxReportDao.getOutTaxDetail(schemaLabel,map);
    }
}
