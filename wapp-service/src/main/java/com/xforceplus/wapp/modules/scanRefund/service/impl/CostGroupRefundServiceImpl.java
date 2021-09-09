package com.xforceplus.wapp.modules.scanRefund.service.impl;



import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.dao.CostGroupRefundDao;
import com.xforceplus.wapp.modules.scanRefund.dao.GenerateRefundNumberDao;
import com.xforceplus.wapp.modules.scanRefund.entity.CostGroupRefundEntity;
import com.xforceplus.wapp.modules.scanRefund.service.CostGroupRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CostGroupRefundServiceImpl implements CostGroupRefundService {

    @Autowired
    private CostGroupRefundDao costGroupRefundDao;
    private GenerateRefundNumberDao generateRefundNumberDao;

    @Override
    public List<CostGroupRefundEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return costGroupRefundDao.queryList(schemaLabel,map);
    }

    @Override
    public List<CostGroupRefundEntity> getRecordInvoiceList(Map<String, Object> params) {
        return costGroupRefundDao.getRecordInvoiceList(params);
    }

    @Override
    public Integer getRecordInvoiceListCount(Map<String, Object> params) {
        return costGroupRefundDao.getRecordInvoiceListCount(params);
    }

    @Override
    public List<CostGroupRefundEntity> getRateList(Map<String, Object> params) {
        return costGroupRefundDao.getRateList(params);
    }

    @Override
    public CostGroupRefundEntity getRateListTotal(Map<String, Object> params) {
        return costGroupRefundDao.getRateListTotal(params);
    }

    @Override
    public Integer getRateListCount(Map<String, Object> params) {
        return costGroupRefundDao.getRateListCount(params);
    }

    @Override
    public List<CostGroupRefundEntity> getCostList(Map<String, Object> params) {
        return costGroupRefundDao.getCostList(params);
    }

    @Override
    public Integer getCostListCount(Map<String, Object> params) {
        return costGroupRefundDao.getCostListCount(params);
    }


    @Override
    public void inputrefundnotes(String schemaLabel,String uuid,String refundNotes,String rebateNo) {
        costGroupRefundDao.inputrefundnotes(schemaLabel,uuid, refundNotes,rebateNo);
    }

    @Override
    public void inputrefundyesno(String uuid,String refundReason) {
        costGroupRefundDao.inputrefundyesno(uuid,refundReason);
    }


    @Override
    public CostGroupRefundEntity querymaxrebateno() {
        return costGroupRefundDao.querymaxrebateno();
    }

    @Override
    public List<CostGroupRefundEntity> queryuuid(Long id) {
        return costGroupRefundDao.queryuuid(id);
    }
//    @Override
//    public GroupRefundEntity queryuuid(String schemaLabel,Long id) {
//        return groupRefundDao.queryuuid(schemaLabel,id);
//    }

    @Override
    public CostGroupRefundEntity queryReason(Long id) {
        return costGroupRefundDao.queryReason(id);
    }

    @Override
    public void updaterecordinvoice(String schemaLabel,Long id) {
        costGroupRefundDao.updaterecordinvoice(schemaLabel,id);
    }


    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return costGroupRefundDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<CostGroupRefundEntity> queryListAll(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return costGroupRefundDao.queryListAll(schemaLabel,map);
    }


}
