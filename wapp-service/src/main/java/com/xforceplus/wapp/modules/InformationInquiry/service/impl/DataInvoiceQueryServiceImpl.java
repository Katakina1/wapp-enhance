package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.InformationInquiry.dao.DataInvoiceQueryDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.MatchEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.DataInvoiceQueryService;

import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/14 20:29
 */
@Service
public class DataInvoiceQueryServiceImpl implements DataInvoiceQueryService {

    @Autowired
    private DataInvoiceQueryDao dataInvoiceQueryDao;
    @Override
    public List<MatchEntity> matchlist(Map<String, Object> map){
        return dataInvoiceQueryDao.matchlist(map);
    }
    @Override
    public Integer matchlistCount(Map<String, Object> map){
        return dataInvoiceQueryDao.matchlistCount(map);
    }
    @Override
    public List<GfOptionEntity> searchGf(){
        return dataInvoiceQueryDao.searchGf();
    }
}
