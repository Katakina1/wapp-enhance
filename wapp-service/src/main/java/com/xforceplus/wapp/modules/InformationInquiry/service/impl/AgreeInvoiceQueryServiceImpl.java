package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.InformationInquiry.dao.AgreeInvoiceQueryDao;
import com.xforceplus.wapp.modules.InformationInquiry.service.AgreeInvoiceQueryService;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/14 20:29
 */
@Service
public class AgreeInvoiceQueryServiceImpl implements AgreeInvoiceQueryService {

    @Autowired
    private AgreeInvoiceQueryDao agreeInvoiceQueryDao;
    @Override
    public PagedQueryResult<MatchEntity> getMatchList(Map<String, Object> params) {
        List<MatchEntity> list = Lists.newArrayList();
        final Integer count =agreeInvoiceQueryDao.getMatchCount(params);
        if(count>0){
            list=agreeInvoiceQueryDao.getMatchList(params);
        }

        return new PagedQueryResult<MatchEntity>(list,count);
    }
}
