package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.InputTaxDetailReportDao;
import com.xforceplus.wapp.modules.report.entity.RateAmountEntity;
import com.xforceplus.wapp.modules.report.service.InputTaxDetailReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InputTaxDetailReportServiceImpl implements InputTaxDetailReportService {

    @Autowired
    InputTaxDetailReportDao inputTaxDetailReportDao;

    @Override
    public Integer getNoneDetailCount(String schemaLabel,Map<String, Object> map) {
        return inputTaxDetailReportDao.getNoneDetailCount(schemaLabel,map);
    }

    @Override
    public List<RateAmountEntity> getRateData(String schemaLabel, Map<String, Object> map) {
        return inputTaxDetailReportDao.getRateData(schemaLabel,map);
    }
}
