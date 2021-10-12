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

    TXfRedNotificationDetailEntity itemInfoToEntity(RedNotificationItem redNotificationItem);

    List<TXfRedNotificationDetailEntity> itemInfoToEntityList(List<RedNotificationItem> redNotificationItemList);

}
