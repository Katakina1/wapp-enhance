package com.xforceplus.wapp.modules.rednotification.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.dao.TXfRedNotificationDetailDao;
import com.xforceplus.wapp.repository.dao.TXfRedNotificationLogDao;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationLogEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedNotificationLogService extends ServiceImpl<TXfRedNotificationLogDao, TXfRedNotificationLogEntity> {


    public boolean saveBatch(List<TXfRedNotificationLogEntity> entityList) {
        return super.saveBatch(entityList);
    }
}
