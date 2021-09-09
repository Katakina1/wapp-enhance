package com.xforceplus.wapp.modules.businessData.service.impl;


import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.businessData.dao.AgreementDao;
import com.xforceplus.wapp.modules.businessData.entity.AgreementEntity;
import com.xforceplus.wapp.modules.businessData.service.AgreementService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class AgreementServiceImpl implements AgreementService {
    private static final Logger LOGGER= getLogger(AgreementServiceImpl.class);
    private final AgreementDao agreementDao;
    @Autowired
    public AgreementServiceImpl(AgreementDao agreementDao){
        this.agreementDao=agreementDao;
    }

    @Override
    public List<AgreementEntity> getAgreementList(Map<String, Object> map){
        return agreementDao.getAgreementList(map);
    }

    @Override
    public Integer agreementQueryCount(Map<String, Object> map){
        return agreementDao.agreementQueryCount(map);
    }

    @Override
    public List<AgreementEntity> getAgreementListBy(Map<String, Object> map){
        List<AgreementEntity> list= agreementDao.getAgreementList(map);
        for (int i=0;i<list.size();i++){
            AgreementEntity agreementEntity=list.get(i);
            if(agreementEntity.getRedticketDataSerialNumber()!=null){
                list.remove(i);
            }
        }
        return list;
    }

    @Override
    public Integer agreementQueryRedCount(Map<String, Object> map){ return agreementDao.agreementQueryRedCount(map); }
}
