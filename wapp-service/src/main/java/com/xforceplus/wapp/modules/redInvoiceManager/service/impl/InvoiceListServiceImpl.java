package com.xforceplus.wapp.modules.redInvoiceManager.service.impl;



import com.xforceplus.wapp.modules.redInvoiceManager.dao.InvoiceListDao;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InvoiceListService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InvoiceListServiceImpl implements InvoiceListService {

    @Autowired
    private InvoiceListDao invoiceListDao;

    @Override
    public List<UploadScarletLetterEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return invoiceListDao.queryList(schemaLabel,map);
    }

    @Override
    public List<UploadScarletLetterEntity> queryListAll( Map<String, Object> map) {
        return invoiceListDao.queryListAll(map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map) {

        return invoiceListDao.queryTotalResult(schemaLabel,map);
    }


    @Override
    public List<InvoiceListEntity> getRedInvoiceList(Map<String, Object> params) {
        return invoiceListDao.getRedInvoiceList(params);
    }

    @Override
    public Integer getRedInvoiceCount(Map<String, Object> params) {
        return invoiceListDao.getRedInvoiceCount(params);
    }

    @Override
    public List<InvoiceListEntity> getRedInvoiceDetailList(String invoiceCode,String invoiceNo) {
        return invoiceListDao.getRedInvoiceDetailList(invoiceCode,invoiceNo);
    }

}
