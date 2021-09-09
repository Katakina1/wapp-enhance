package com.xforceplus.wapp.modules.scanRefund.service.impl;



import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.dao.HostRefundDao;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import com.xforceplus.wapp.modules.scanRefund.service.HostRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HostRefundServiceImpl implements HostRefundService {

    @Autowired
    private HostRefundDao hostRefundDao;

    @Override
    public List<GroupRefundEntity> queryList( Map<String, Object> map) {
        return hostRefundDao.queryList(map);
    }

    @Override
    public List<GroupRefundEntity> queryRzhList( Map<String, Object> map) {
        return hostRefundDao.queryRzhList(map);
    }

    @Override
    public void inputrefundyesno(String uuid,String refundReason) {
        hostRefundDao.inputrefundyesno(uuid,refundReason);
    }

    @Override
    public List<GroupRefundEntity> queryuuid(Long id) {
        return hostRefundDao.queryuuid(id);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(Map<String, Object> map) {
        return hostRefundDao.queryTotalResult(map);
    }

    @Override
    public ReportStatisticsEntity queryRzhTotalResult(Map<String, Object> map) {
        return hostRefundDao.queryRzhTotalResult(map);
    }

    @Override
    public GroupRefundEntity queryisdel(String uuid) {
        return hostRefundDao.queryisdel(uuid);
    }

    @Override
    public Integer getuuidCount(String uuid) {
        return hostRefundDao.getuuidCount(uuid);
    }

    @Override
    public int saveInvoice(GenerateBindNumberEntity entity) {
        return hostRefundDao.saveInvoice(entity);
    }

    @Override
    public GenerateBindNumberEntity queryListUuid(String uuid) {
        return hostRefundDao.queryListUuid(uuid);
    }



}
