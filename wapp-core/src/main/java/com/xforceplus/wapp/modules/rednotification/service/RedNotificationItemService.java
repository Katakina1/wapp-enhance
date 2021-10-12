package com.xforceplus.wapp.modules.rednotification.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class RedNotificationItemService extends ServiceImpl<TXfRedNotificationDetailDao, TXfRedNotificationDetailEntity> {


    public boolean saveBatch(List<TXfRedNotificationDetailEntity> entityList) {
        return super.saveBatch(entityList);
    }
}
