package com.xforceplus.wapp.modules.rednotification.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.rednotification.mapstruct.RedNotificationMainMapper;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.repository.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.List;

@Service
public class RedNotificationMainService extends ServiceImpl<TXfRedNotificationDao, TXfRedNotificationEntity> {

    @Autowired
    RedNotificationMainMapper redNotificationMainMapper;
    @Autowired
    RedNotificationItemService redNotificationItemService;


    public String add(AddRedNotificationRequest request) {

       // 保存红字信息 进入待审核
        List<TXfRedNotificationEntity> listMain = Lists.newLinkedList();
        List<TXfRedNotificationDetailEntity> listItem = Lists.newLinkedList();
        List<RedNotificationInfo> redNotificationInfoList = request.getRedNotificationInfoList();
        redNotificationInfoList.stream().forEach(info->{
            TXfRedNotificationEntity tXfRedNotificationEntity = redNotificationMainMapper.mainInfoToEntity(info.getRednotificationMain());
            List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities = redNotificationMainMapper.itemInfoToEntityList(info.getRedNotificationItemList());

            listMain.add(tXfRedNotificationEntity);
            listItem.addAll(tXfRedNotificationDetailEntities);
        });

        saveBatch(listMain);
        redNotificationItemService.saveBatch(listItem);

        return "" ;
    }

    /**
     * 红字信息申请弹窗
     * @param queryModel
     * @return
     */
    public GetTerminalResult getTerminals(QueryModel queryModel) {
       return null;
    }
}
