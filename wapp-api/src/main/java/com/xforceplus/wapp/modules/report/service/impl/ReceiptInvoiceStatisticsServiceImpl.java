package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.ReceiptInvoiceStatisticsDao;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.ReceiptInvoiceStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/12
 * 发票签收统计查询业务层
 */
@Service
public class ReceiptInvoiceStatisticsServiceImpl implements ReceiptInvoiceStatisticsService {

    @Autowired
    private ReceiptInvoiceStatisticsDao receiptInvoiceStatisticsDao;

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(Map<String, Object> map, String schemaLabel) {
        return receiptInvoiceStatisticsDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(Map<String, Object> map, String schemaLabel) {
        return receiptInvoiceStatisticsDao.queryTotalResult(map,schemaLabel);
    }

    @Override
    public int queryTotal(Map<String, Object> map,String schemaLabel) {
        return receiptInvoiceStatisticsDao.queryTotal(schemaLabel,map);
    }

    @Override
    public List<OptionEntity> searchGf(Long userId, String schemaLabel) {
        return receiptInvoiceStatisticsDao.searchGf(userId,schemaLabel);
    }

    @Override
    public List<String> searchXf(Map<String, Object> map,String schemaLabel) {
        return receiptInvoiceStatisticsDao.searchXf(map,schemaLabel);
    }
}
