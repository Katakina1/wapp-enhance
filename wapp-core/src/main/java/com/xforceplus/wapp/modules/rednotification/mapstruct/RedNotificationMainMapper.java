package com.xforceplus.wapp.modules.rednotification.mapstruct;


import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.modules.rednotification.model.excl.ExportInfo;
import com.xforceplus.wapp.modules.rednotification.model.excl.ExportItemInfo;
import com.xforceplus.wapp.modules.rednotification.model.excl.ImportInfo;
import com.xforceplus.wapp.repository.entity.*;
import org.mapstruct.*;
import com.xforceplus.wapp.modules.rednotification.model.*;


import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring", uses = {RedNotificationFactory.class},imports = { ConvertHelper.class})
public interface RedNotificationMainMapper {

    TXfRedNotificationEntity mainInfoToEntity(RedNotificationMain rednotificationMain);

    @InheritInverseConfiguration(name = "mainInfoToEntity")
    RedNotificationMain entityToMainInfo(TXfRedNotificationEntity redNotificationEntity);

    List<RedNotificationMain> entityToMainInfoList(List<TXfRedNotificationEntity> redNotificationEntityList);

    @Mapping(target = "id", expression = "java(IdGenerator.generate())")
    @Mapping(target = "goodsNoVer", expression = "java(ConvertHelper.getGoodsNoVer())")
    TXfRedNotificationDetailEntity itemInfoToEntity(RedNotificationItem redNotificationItem);

    List<TXfRedNotificationDetailEntity> itemInfoToEntityList(List<RedNotificationItem> redNotificationItemList);

    @InheritInverseConfiguration(name = "itemInfoToEntityList")
    List<RedNotificationItem> entityToItemInfoList(List<TXfRedNotificationDetailEntity> redNotificationEntityList);


    ExportInfo mainEntityToExportInfo(TXfRedNotificationEntity apply);

    List<ExportItemInfo> detailEntityToExportInfoList(List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities);


    @Mapping(target = "serialNo", source = "sellerNumber")
    RedNotificationMain importInfoToMainEntity(ImportInfo importInfo);

    List<RedNotificationItem> importInfoListToItemEntityList(List<ImportInfo> importInfoList);

    @Mapping(target = "taxRate", expression = "java(ConvertHelper.handleTaxRate(importInfo.getTaxRate()))")
    RedNotificationItem importInfoToRedNotificationItem(ImportInfo importInfo);
}
