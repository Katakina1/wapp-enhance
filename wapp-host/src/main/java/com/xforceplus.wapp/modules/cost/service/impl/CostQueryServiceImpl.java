package com.xforceplus.wapp.modules.cost.service.impl;

import com.xforceplus.wapp.modules.cost.dao.CostQueryDao;
import com.xforceplus.wapp.modules.cost.entity.*;
import com.xforceplus.wapp.modules.cost.service.CostQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CostQueryServiceImpl implements CostQueryService {

    @Autowired
    private CostQueryDao costQueryDao;

    @Override
    public List<SettlementEntity> queryList(Map<String, Object> map) {
        return costQueryDao.queryList(map);
    }

    @Override
    public Integer queryCount(Map<String, Object> map) {
        return costQueryDao.queryCount(map);
    }

    @Override
    public List<RecordInvoiceEntity> queryDetail(String costNo) {
        List<RecordInvoiceEntity> invoiceList = costQueryDao.getInvoice(costNo);
        for(RecordInvoiceEntity invoice : invoiceList){
            List<RateEntity> rateList = costQueryDao.getRate(costNo, invoice.getInvoiceCode(), invoice.getInvoiceNo());
            for(RateEntity rate : rateList){
                List<CostEntity> costList = costQueryDao.getCost(rate.getId());
                rate.setCostTableData(costList);
            }
            invoice.setRateTableData(rateList);
        }
        return invoiceList;
    }

    @Override
    public List<SettlementFileEntity> queryFileDetail(String costNo) {
        return costQueryDao.getFile(costNo);
    }

    @Override
    public List<SelectionOptionEntity> getStatusOptions() {
        return costQueryDao.getStatusOptions();
    }

    @Override
    public String formatEmail(String staffNo) {
        return costQueryDao.formatEmail(staffNo);
    }
}
