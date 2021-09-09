package com.xforceplus.wapp.modules.collect.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.collect.dao.InvoiceCollectionDao;
import com.xforceplus.wapp.modules.collect.entity.CollectListExcelEntity;
import com.xforceplus.wapp.modules.collect.entity.CollectListStatistic;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.service.InvoiceCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 发票采集列表业务层实现
 * @author Colin.hu
 * @date 4/11/2018
 */
@Service
public class InvoiceCollectionServiceImpl implements InvoiceCollectionService {

    private final InvoiceCollectionDao invoiceCollectionDao;

    @Autowired
    public InvoiceCollectionServiceImpl(InvoiceCollectionDao invoiceCollectionDao) {
        this.invoiceCollectionDao = invoiceCollectionDao;
    }

    @Override
    public PagedQueryResult<CollectListStatistic> selectInvoiceCollection(Map<String, Object> map) {
        final PagedQueryResult<CollectListStatistic> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = invoiceCollectionDao.getInvoiceCollectionCount(map);

        //需要返回的集合
        List<CollectListStatistic> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = invoiceCollectionDao.selectInvoiceCollection(map);
            final Map<String, BigDecimal> totalMap = invoiceCollectionDao.getSumAmount(map);
            pagedQueryResult.setSummationTotalAmount(totalMap.get("summationTotalAmount"));
            pagedQueryResult.setSummationTaxAmount(totalMap.get("summationTaxAmount"));
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public List<Map<String, String>> getGfNameByUserId(String schemaLabel, Long userId) {
        return invoiceCollectionDao.getGfNameByUserId(schemaLabel, userId);
    }

    @Override
    public PagedQueryResult<InvoiceCollectionInfo> getInvoiceInfo(Map<String, Object> map) {
        final PagedQueryResult<InvoiceCollectionInfo> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = invoiceCollectionDao.getInvoiceInfoCount(map);

        //需要返回的集合
        List<InvoiceCollectionInfo> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = invoiceCollectionDao.getInvoiceInfo(map);
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }
    @Override
    public List<CollectListExcelEntity> toExcel(List<CollectListStatistic> list){
        List<CollectListExcelEntity> list2=new ArrayList<>();
        for (CollectListStatistic cs:list) {
            CollectListExcelEntity ce=new CollectListExcelEntity();
            ce.setCreateDate(cs.getCreateDate());
            ce.setGfName(cs.getGfName());
            ce.setGfTaxNo(cs.getGfTaxNo());
            ce.setCollectCount(cs.getCollectCount().toString());
            ce.setSumTaxAmount(cs.getSumTaxAmount());
            ce.setSumTotalAmount(cs.getSumTotalAmount());
            list2.add(ce);
        }
        return list2;
    }
}
