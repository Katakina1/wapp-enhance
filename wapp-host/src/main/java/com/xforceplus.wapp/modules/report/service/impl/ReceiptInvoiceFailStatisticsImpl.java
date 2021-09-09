package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.ReceiptInvoiceFailStatisticsDao;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.ReceiptInvoiceFailStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/14
 * 发票签收失败统计业务层
 */
@Service
public class ReceiptInvoiceFailStatisticsImpl implements ReceiptInvoiceFailStatisticsService {

    @Autowired
    private ReceiptInvoiceFailStatisticsDao receiptInvoiceFailStatisticsDao;

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(Map<String, Object> map, String schemaLabel) {
        return receiptInvoiceFailStatisticsDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(Map<String, Object> map, String schemaLabel) {
        return receiptInvoiceFailStatisticsDao.queryTotalResult(map,schemaLabel);
    }

    @Override
    public int queryTotal(Map<String, Object> map, String schemaLabel) {
        return receiptInvoiceFailStatisticsDao.queryTotal(schemaLabel,map);
    }

    @Override
    public List<OptionEntity> searchGf(Long userId, String schemaLabel) {
        return receiptInvoiceFailStatisticsDao.searchGf(userId,schemaLabel);
    }

    @Override
    public List<String> searchXf(Map<String, Object> map, String schemaLabel) {
        return receiptInvoiceFailStatisticsDao.searchXf(map,schemaLabel);
    }
}
