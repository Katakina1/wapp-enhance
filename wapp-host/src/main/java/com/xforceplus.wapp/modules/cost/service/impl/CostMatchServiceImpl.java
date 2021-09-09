package com.xforceplus.wapp.modules.cost.service.impl;

import com.xforceplus.wapp.modules.cost.dao.CostMatchDao;
import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementMatchEntity;
import com.xforceplus.wapp.modules.cost.service.CostMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CostMatchServiceImpl implements CostMatchService {

    @Autowired
    private CostMatchDao costMatchDao;

    @Override
    public List<SettlementMatchEntity> queryList(Map<String, Object> map) {
        return costMatchDao.queryList(map);
    }

    @Override
    public Integer queryCount(Map<String, Object> map) {
        return costMatchDao.queryCount(map);
    }

    @Override
    public List<CostEntity> queryDetail(String costNo) {
        return costMatchDao.queryDetail(costNo);
    }

    @Override
    public List<CostEntity> querySelectDetail(String[] costNoArray) {
        return costMatchDao.querySelectDetail(costNoArray);
    }

    @Override
    public int selectInvoice(RecordInvoiceEntity entity) {
        return costMatchDao.selectInvoice(entity);
    }
}
