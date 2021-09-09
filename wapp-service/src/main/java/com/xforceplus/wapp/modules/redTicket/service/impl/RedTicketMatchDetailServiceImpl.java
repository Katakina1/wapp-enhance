package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.modules.redTicket.dao.GenerateRedTicketInformationDao;
import com.xforceplus.wapp.modules.redTicket.dao.RedTicketMatchDetailDao;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;
import com.xforceplus.wapp.modules.redTicket.service.RedTicketMatchDetailService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class RedTicketMatchDetailServiceImpl implements RedTicketMatchDetailService {
    private static final Logger LOGGER= getLogger(RedTicketMatchDetailServiceImpl.class);
    private final RedTicketMatchDetailDao redTicketMatchDetailDao;
    private final GenerateRedTicketInformationDao generateRedTicketInformationDao;
    @Autowired
    public RedTicketMatchDetailServiceImpl(RedTicketMatchDetailDao redTicketMatchDetailDao,GenerateRedTicketInformationDao generateRedTicketInformationDao){
        this.redTicketMatchDetailDao=redTicketMatchDetailDao;
        this.generateRedTicketInformationDao=generateRedTicketInformationDao;
    }
    @Override
    @Transactional
    public Map<String, Object> updateInvoiceDetaillist(RedTicketMatchDetail params,Integer userId){
        List<RedTicketMatchDetail> detailnow=params.getDetailsnow();
        String uuidNow;
        List<RedTicketMatchDetail> detailbefore=params.getDetailbefore();
        Map<String, Object> map=new HashMap<String, Object>();
        List<RedTicketMatchDetail> nowDetail=Lists.newArrayList();
        try {



        }catch(Exception e){
            LOGGER.info("红冲失败 {}",e);
            throw new RuntimeException();
        }
        return map;

    }
    @Override
    public Integer invoiceDetailsRedRushCount(Map<String, Object> map){
        return redTicketMatchDetailDao.getRedRushDetails(map);

    }
}
