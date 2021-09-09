package com.xforceplus.wapp.modules.scanRefund.service.impl;



import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.dao.GenerateRefundNumberDao;
import com.xforceplus.wapp.modules.scanRefund.dao.GroupRefundDao;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import com.xforceplus.wapp.modules.scanRefund.service.GroupRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GroupRefundServiceImpl implements GroupRefundService {

    @Autowired
    private GroupRefundDao groupRefundDao;
    private GenerateRefundNumberDao generateRefundNumberDao;

    @Override
    public List<GroupRefundEntity> queryList( Map<String, Object> map) {
        return groupRefundDao.queryList(map);
    }

    @Override
    public List<GroupRefundEntity> getRecordInvoiceList(Map<String, Object> params) {
        return groupRefundDao.getRecordInvoiceList(params);
    }

    @Override
    public Integer getRecordInvoiceListCount(Map<String, Object> params) {
        return groupRefundDao.getRecordInvoiceListCount(params);
    }

    @Override
    public List<GroupRefundEntity> getPOList(Map<String, Object> params) {
        return groupRefundDao.getPOList(params);
    }

    @Override
    public Integer getPOListCount(Map<String, Object> params) {
        return groupRefundDao.getPOListCount(params);
    }

    @Override
    public List<GroupRefundEntity> getClaimList(Map<String, Object> params) {
        return groupRefundDao.getClaimList(params);
    }

    @Override
    public Integer getClaimListCount(Map<String, Object> params) {
        return groupRefundDao.getClaimListCount(params);
    }


    @Override
    public void inputrefundnotes(String schemaLabel,String uuid,String refundNotes,String rebateNo) {
        groupRefundDao.inputrefundnotes(schemaLabel,uuid, refundNotes,rebateNo);
    }

    @Override
    public void inputrefundyesno(String uuid,String refundReason) {
        groupRefundDao.inputrefundyesno(uuid,refundReason);
    }


    @Override
    public GroupRefundEntity querymaxrebateno() {
        return groupRefundDao.querymaxrebateno();
    }

    @Override
    public List<GroupRefundEntity> queryuuid(Long id) {
        return groupRefundDao.queryuuid(id);
    }

    @Override
    public GroupRefundEntity queryReason(Long id) {
        return groupRefundDao.queryReason(id);
    }
//    @Override
//    public GroupRefundEntity queryuuid(String schemaLabel,Long id) {
//        return groupRefundDao.queryuuid(schemaLabel,id);
//    }

    @Override
    public void updaterecordinvoice(String schemaLabel,Long id) {
        groupRefundDao.updaterecordinvoice(schemaLabel,id);
    }


    @Override
    public ReportStatisticsEntity queryTotalResult(Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return groupRefundDao.queryTotalResult(map);
    }

    @Override
    public GroupRefundEntity queryisdel(String uuid) {
        return groupRefundDao.queryisdel(uuid);
    }

    @Override
    public List<GroupRefundEntity> queryListAll(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return groupRefundDao.queryListAll(schemaLabel,map);
    }


}
