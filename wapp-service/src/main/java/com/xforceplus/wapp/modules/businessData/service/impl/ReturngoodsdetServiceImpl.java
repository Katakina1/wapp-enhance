package com.xforceplus.wapp.modules.businessData.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.businessData.dao.ReturngoodsdetDao;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsEntity;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsdetEntity;
import com.xforceplus.wapp.modules.businessData.service.ReturngoodsdetService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ReturngoodsdetServiceImpl implements ReturngoodsdetService {
    private static final Logger LOGGER= getLogger(ReturngoodsServiceImpl.class);
    private final ReturngoodsdetDao returngoodsdetDao;
    @Autowired
    public ReturngoodsdetServiceImpl(ReturngoodsdetDao returngoodsdetDao){
        this.returngoodsdetDao=returngoodsdetDao;
    }

    @Override
    public List<ReturngoodsdetEntity> getReturnGoodsdetList(Map<String, Object> map) { return returngoodsdetDao.getReturnGoodsDetList(map); }

    @Override
    public Integer returnGoodsDetQueryCount(Map<String, Object> map) { return returngoodsdetDao.returnGoodsDetQueryCount(map);}
}
