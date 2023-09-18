package com.xforceplus.wapp.modules.rednotification.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.dao.TXfRedNotificationDetailDao;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationDetailEntity;

@Service
public class RedNotificationItemService extends ServiceImpl<TXfRedNotificationDetailDao, TXfRedNotificationDetailEntity> {


    public boolean saveBatch(List<TXfRedNotificationDetailEntity> entityList) {
		if (entityList != null && entityList.size() > 0) {
			entityList.forEach(item -> {
				if (item.getCreateDate() == null) {
					item.setCreateDate(new Date());
				}
				if (item.getUpdateDate() == null) {
					item.setUpdateDate(item.getCreateDate());
				}
			});
		}
        return super.saveBatch(entityList);
    }
}
