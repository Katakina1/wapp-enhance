package com.xforceplus.wapp.modules.fixed.service.impl;



import com.xforceplus.wapp.modules.fixed.dao.IndexGroupRefundDao;
import com.xforceplus.wapp.modules.fixed.entity.IndexGroupRefundEntity;
import com.xforceplus.wapp.modules.fixed.service.IndexGroupRefundService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IndexGroupRefundServiceImpl implements IndexGroupRefundService {

    @Autowired
    private IndexGroupRefundDao indexGroupRefundDao;

    @Override
    public List<IndexGroupRefundEntity> queryList(Map<String, Object> map) {
        return indexGroupRefundDao.queryList(map);
    }

    @Override
    public List<IndexGroupRefundEntity> getRecordInvoiceList(Map<String, Object> params) {
        return indexGroupRefundDao.getRecordInvoiceList(params);
    }

    @Override
    public Integer getRecordInvoiceListCount(Map<String, Object> params) {
        return indexGroupRefundDao.getRecordInvoiceListCount(params);
    }

    @Override
    public List<IndexGroupRefundEntity> getPOList(Map<String, Object> params) {
        return indexGroupRefundDao.getPOList(params);
    }

    @Override
    public Integer getPOListCount(Map<String, Object> params) {
        return indexGroupRefundDao.getPOListCount(params);
    }

    @Override
    public List<IndexGroupRefundEntity> getClaimList(Map<String, Object> params) {
        return indexGroupRefundDao.getClaimList(params);
    }

    @Override
    public Integer getClaimListCount(Map<String, Object> params) {
        return indexGroupRefundDao.getClaimListCount(params);
    }

    @Override
    public void inputrefundnotes(String schemaLabel,String uuid,String refundNotes,String rebateNo) {
        indexGroupRefundDao.inputrefundnotes(schemaLabel,uuid, refundNotes,rebateNo);
    }


    @Override
    public IndexGroupRefundEntity querymaxrebateno() {
        return indexGroupRefundDao.querymaxrebateno();
    }

    @Override
    public List<IndexGroupRefundEntity> queryuuid(Long id) {
        return indexGroupRefundDao.queryuuid(id);
    }

    @Override
    public void updaterecordinvoice(String schemaLabel,Long id) {
        indexGroupRefundDao.updaterecordinvoice(schemaLabel,id);
    }


    @Override
    public ReportStatisticsEntity queryTotalResult(Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return indexGroupRefundDao.queryTotalResult(map);
    }

    @Override
    public List<IndexGroupRefundEntity> queryListAll(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return indexGroupRefundDao.queryListAll(schemaLabel,map);
    }

    @Override
    public void inputrefundyesno(String uuid,String refundReason) {
        indexGroupRefundDao.inputrefundyesno(uuid,refundReason);
    }

    @Override
    public IndexGroupRefundEntity queryReason(Long id) {
        return indexGroupRefundDao.queryReason(id);
    }


}
