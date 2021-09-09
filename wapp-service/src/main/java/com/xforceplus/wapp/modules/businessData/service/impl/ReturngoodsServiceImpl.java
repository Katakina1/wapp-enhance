package com.xforceplus.wapp.modules.businessData.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.businessData.dao.ReturngoodsDao;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsEntity;
import com.xforceplus.wapp.modules.businessData.service.ReturngoodsService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ReturngoodsServiceImpl implements ReturngoodsService {
    private static final Logger LOGGER= getLogger(ReturngoodsServiceImpl.class);
    private final ReturngoodsDao returngoodsDao;
    @Autowired
    public ReturngoodsServiceImpl(ReturngoodsDao returngoodsDao){
        this.returngoodsDao=returngoodsDao;
    }

    @Override
    public List<ReturngoodsEntity> getReturnGoodsListBy(Map<String, Object> map) {
        List<ReturngoodsEntity> list=returngoodsDao.getReturnGoodsList(map);
        for (int i=0;i<list.size();i++){
            ReturngoodsEntity returngoodsEntity=list.get(i);
            if(returngoodsEntity.getRedticketDataSerialNumber()!=null){
                list.remove(i);
            }
        }
        return list;
    }

    @Override
    public List<ReturngoodsEntity> getReturnGoodsList(Map<String, Object> map) { return returngoodsDao.getReturnGoodsList(map); }

    @Override
    public Integer returnGoodsQueryCount(Map<String, Object> map) {
        return returngoodsDao.returnGoodsQueryCount(map);
    }

    @Override
    public Integer returnGoodsQueryRedCount(Map<String, Object> map) { return returngoodsDao.returnGoodsQueryRedCount(map); }
}
