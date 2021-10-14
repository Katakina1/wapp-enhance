package com.xforceplus.wapp.modules.rednotification.mapstruct;


import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.repository.entity.*;
import org.mapstruct.*;
import com.xforceplus.wapp.modules.rednotification.model.*;


import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring", uses = {RedNotificationFactory.class})
public interface RedNotificationMainMapper {

    TXfRedNotificationEntity mainInfoToEntity(RedNotificationMain rednotificationMain);

    @InheritInverseConfiguration(name = "mainInfoToEntity")
    RedNotificationMain entityToMainInfo(TXfRedNotificationEntity redNotificationEntity);

    List<RedNotificationMain> entityToMainInfoList(List<TXfRedNotificationEntity> redNotificationEntityList);

    TXfRedNotificationDetailEntity itemInfoToEntity(RedNotificationItem redNotificationItem);

    List<TXfRedNotificationDetailEntity> itemInfoToEntityList(List<RedNotificationItem> redNotificationItemList);

    @InheritInverseConfiguration(name = "itemInfoToEntityList")
    List<RedNotificationItem> entityToItemInfoList(List<TXfRedNotificationDetailEntity> redNotificationEntityList);


}
