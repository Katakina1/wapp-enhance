package com.xforceplus.wapp.modules.rednotification.mapstruct;


import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationItem;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.modules.rednotification.model.excl.ExportInfo;
import com.xforceplus.wapp.modules.rednotification.model.excl.ExportItemInfo;
import com.xforceplus.wapp.modules.rednotification.model.excl.ImportInfo;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BaseConverter.class, RedNotificationFactory.class},imports = { ConvertHelper.class})
public interface RedNotificationMainMapper {
    @Mapping(target = "originInvoiceNo", source = "originalInvoiceNo")
    @Mapping(target = "originInvoiceCode", source = "originalInvoiceCode")
    //下面这行可能存在历史错误行未注释
//    @Mapping(target = "invoiceDate", source = "originalInvoiceDate")
    @Mapping(target = "originInvoiceDate", source = "originalInvoiceDate")
    TXfRedNotificationEntity mainInfoToEntity(RedNotificationMain rednotificationMain);

    @InheritInverseConfiguration(name = "mainInfoToEntity")
    RedNotificationMain entityToMainInfo(TXfRedNotificationEntity redNotificationEntity);

    List<RedNotificationMain> entityToMainInfoList(List<TXfRedNotificationEntity> redNotificationEntityList);

    @Mapping(target = "id", expression = "java(IdGenerator.generate())")
    @Mapping(target = "goodsNoVer", expression = "java(ConvertHelper.getGoodsNoVer(redNotificationItem.getGoodsNoVer()))")
    TXfRedNotificationDetailEntity itemInfoToEntity(RedNotificationItem redNotificationItem);

    List<TXfRedNotificationDetailEntity> itemInfoToEntityList(List<RedNotificationItem> redNotificationItemList);

    @InheritInverseConfiguration(name = "itemInfoToEntityList")
    List<RedNotificationItem> entityToItemInfoList(List<TXfRedNotificationDetailEntity> redNotificationEntityList);



    ExportInfo mainEntityToExportInfo(TXfRedNotificationEntity apply);


    @Mapping(target = "billNo", source = "dto.billNo")
    @Mapping(target = "serialNo", source = "dto.serialNo")
    @Mapping(target = "goodsName", source = "entity.goodsName")
    @Mapping(target = "model", source = "entity.model")
    @Mapping(target = "unit", source = "entity.unit")
    @Mapping(target = "amountWithTax", source = "entity.amountWithTax")
    @Mapping(target = "amountWithoutTax", source = "entity.amountWithoutTax")
    @Mapping(target = "taxAmount", source = "entity.taxAmount")
    @Mapping(target = "num", source = "entity.num")
    @Mapping(target = "unitPrice", source = "entity.unitPrice")
    ExportItemInfo detailEntityToExportInfo(TXfRedNotificationDetailEntity entity, ExportInfo dto);



    @Mapping(target = "serialNo", source = "sellerNumber")
    @Mapping(target = "applyPerson", source = "userName")
    @Mapping(target = "applyPersonTel", source = "tel")
    @Mapping(target = "oilMemo", source = "oliApplyReason")
    @Mapping(target = "originInvoiceType", source = "originInvoiceType")
    @Mapping(target = "originalInvoiceCode", source = "originInvoiceCode")
    @Mapping(target = "originalInvoiceNo", source = "originInvoiceNo")
    @Mapping(target = "originalInvoiceDate", source = "invoiceDate")
    RedNotificationMain importInfoToMainEntity(ImportInfo importInfo);

    List<RedNotificationItem> importInfoListToItemEntityList(List<ImportInfo> importInfoList);

    @Mapping(target = "taxRate", expression = "java(ConvertHelper.handleTaxRate(importInfo.getTaxRate()))")
    RedNotificationItem importInfoToRedNotificationItem(ImportInfo importInfo);
}

