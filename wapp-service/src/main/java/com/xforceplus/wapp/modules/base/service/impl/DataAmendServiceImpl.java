package com.xforceplus.wapp.modules.base.service.impl;


import com.xforceplus.wapp.modules.base.dao.DataAmendDao;
import com.xforceplus.wapp.modules.base.entity.DataAmendEntity;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.DataAmendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/14 20:29
 */
@Service
public class DataAmendServiceImpl implements DataAmendService {

    @Autowired
    private DataAmendDao dataAmendDao;
    @Override
    public List<DataAmendEntity> queryList(Map<String, Object> map){
        return dataAmendDao.queryList(map);
    }
    @Override
    public Integer queryListCount(Map<String, Object> map){
        return dataAmendDao.queryListCount(map);
    }

    @Override
    public Integer update(DataAmendEntity dataAmendEntity) {
     return dataAmendDao.updates(dataAmendEntity);
    }
    //@Override
//    public Boolean update(String schemaLabel, Map<String, Object> params){
//        return dataAmendDao.update(entity,user);
//    }


//    @Override
//    public List<GfOptionEntity> searchGf(){
//        return dataInvoiceQueryDao.searchGf();
//    }




}
