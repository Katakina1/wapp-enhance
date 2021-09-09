package com.xforceplus.wapp.modules.cost.service.impl;

import com.xforceplus.wapp.modules.cost.dao.CostPushDao;
import com.xforceplus.wapp.modules.cost.dao.CostQueryDao;
import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.entity.InvoiceRateEntity;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import com.xforceplus.wapp.modules.cost.service.CostPushService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostPushServiceImpl implements CostPushService {

    @Autowired
    private CostPushDao costPushDao;

    @Autowired
    private CostQueryDao costQueryDao;

    @Override
    public List<SettlementEntity> getPushData(String costNo) {
        List<SettlementEntity> list = costPushDao.getMainData(costNo);
        for(SettlementEntity mainData : list){
            List<InvoiceRateEntity> rateList = costPushDao.getInvoiceRate(mainData.getCostNo());
            for(InvoiceRateEntity rate : rateList){
                List<CostEntity> costList = costQueryDao.getCost(rate.getId());
                rate.setCostTableData(costList);
            }
            mainData.setInvoiceRateList(rateList);
            mainData.setFileList(costQueryDao.getFile(costNo));
        }
        return list;
    }

    @Override
    public Integer saveInstanceId(String costNo, String instanceId) {
        return costPushDao.saveInstanceId(costNo, instanceId);
    }

    @Override
    public Integer saveCostId(Long id, String instanceId, String bpmsId) {
        return costPushDao.saveCostId(id, instanceId, bpmsId);
    }

    @Override
    public List<SettlementEntity> getMainInstanceId() {
        return costPushDao.getMainInstanceId();
    }

    @Override
    public Integer updateStatus(String costNo, String status) {
        return costPushDao.updateStatus(costNo, status);
    }

    @Override
    public List<CostEntity> getCostId(String instanceId) {
        return costPushDao.getCostId(instanceId);
    }

    @Override
    public Integer updateMain(SettlementEntity entity) {
        return costPushDao.updateMain(entity);
    }

    @Override
    public Integer updateCost(CostEntity entity) {
        return costPushDao.updateCost(entity);
    }

    @Override
    public Integer cancelMatch(String costNo) {
        return costPushDao.cancelMatch(costNo);
    }

    @Override
    public void updateRecord2Confirm(String costNo) {
          List<String> uuids = costPushDao.queryInvoicesByCostNo(costNo);
          for (int i = 0;i<=uuids.size();i++) {
              costPushDao.updateRecord2Confirm(uuids.get(i));
          }
    }

    @Override
    public void updateRebackInfo(String costNo, String msg) {
        costPushDao.updateReback(costNo,msg);
        costPushDao.deleteSettlementInvice(costNo);
    }
    @Override
    public Integer getCostDeptId(String costDeptId, String invoiceCode,String invoiceNo){
        return costPushDao.getCostDeptId(costDeptId,invoiceCode,invoiceNo);
    }
    @Override
    public List<RecordInvoiceEntity> queryInvoicesByCostNos(String costNo){
        return costPushDao.queryInvoicesByCostNos(costNo);
    }
}
