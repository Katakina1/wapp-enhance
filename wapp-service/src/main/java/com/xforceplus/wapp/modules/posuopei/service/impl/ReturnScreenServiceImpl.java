package com.xforceplus.wapp.modules.posuopei.service.impl;
import com.xforceplus.wapp.modules.posuopei.dao.ReturnScreenDao;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.service.ReturnScreenService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service("returnScreenService")
public class ReturnScreenServiceImpl implements ReturnScreenService {


   private static final Logger LOGGER= getLogger(ReturnScreenServiceImpl.class);
    @Autowired
    ReturnScreenDao returnScreenDao;

   @Transactional
   @Override
   public Integer insertReturnScreen(HostReturnScreenEntity hostReturnScreenEntity){
        return returnScreenDao.insertReturnScreen(hostReturnScreenEntity);
   }

    @Transactional
    @Override
   public PagedQueryResult<HostReturnScreenEntity> getReturnScreenList(Map<String,Object> params){
       List<HostReturnScreenEntity> list = Lists.newArrayList();
       final Integer count =returnScreenDao.getReturnScreenCount(params);
       if(count>0){
           list=returnScreenDao.getReturnScreenList(params);
       }

       return new PagedQueryResult<HostReturnScreenEntity>(list,count);
    }
}
