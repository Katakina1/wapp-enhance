package com.xforceplus.wapp.modules.collect.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.collect.dao.AbnormalInvoiceCollectionDao;
import com.xforceplus.wapp.modules.collect.entity.CollectListStatistic;
import com.xforceplus.wapp.modules.collect.service.AbnormalInvoiceCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 异常发票采集业务层接口实现
 * @author Colin.hu
 * @date 4/11/2018
 */
@Service
public class AbnormalInvoiceCollectionServiceImpl implements AbnormalInvoiceCollectionService {

    private final AbnormalInvoiceCollectionDao abnormalInvoiceCollectionDao;

    @Autowired
    public AbnormalInvoiceCollectionServiceImpl(AbnormalInvoiceCollectionDao abnormalInvoiceCollectionDao) {
        this.abnormalInvoiceCollectionDao = abnormalInvoiceCollectionDao;
    }

    @Override
    public PagedQueryResult<CollectListStatistic> selectAbnormalInvoiceCollection(Map<String, Object> map) {
        final PagedQueryResult<CollectListStatistic> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = abnormalInvoiceCollectionDao.getAbnormalInvoiceCollectionCount(map);

        //需要返回的集合
        List<CollectListStatistic> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = abnormalInvoiceCollectionDao.selectAbnormalInvoiceCollection(map);
            final Map<String, BigDecimal> totalMap = abnormalInvoiceCollectionDao.getAbnormalSumAmount(map);
            pagedQueryResult.setSummationTotalAmount(totalMap.get("summationTotalAmount"));
            pagedQueryResult.setSummationTaxAmount(totalMap.get("summationTaxAmount"));
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }
}
