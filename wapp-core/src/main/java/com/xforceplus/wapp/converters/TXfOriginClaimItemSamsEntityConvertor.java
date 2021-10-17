package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemSamsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TXfOriginClaimItemSamsEntityConvertor {

    TXfOriginClaimItemSamsEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginClaimItemSamsEntityConvertor.class);

    @Mapping(source = "primaryDesc", target = "cnDesc")
    @Mapping(source = "itemTaxPct", target = "taxRate")
    @Mapping(source = "shipCost", target = "amountWithoutTax")
    @Mapping(source = "itemNbr", target = "itemNo")
    @Mapping(source = "rtnDate", target = "verdictDate")
    // @Mapping(source = "shipRetail", target = "")
    @Mapping(source = "deptNbr", target = "deptNbr")
    // @Mapping(source = "claimNumber", target = "")
    @Mapping(source = "vendorNumber", target = "sellerNo")
    @Mapping(source = "storeNbr", target = "storeNbr")
    @Mapping(source = "unit", target = "unit")
    // @Mapping(source = "vendorTaxIdChc", target = "")
    // @Mapping(source = "vendorName", target = "")
    // @Mapping(source = "vendorTaxIdJv", target = "")
    // @Mapping(source = "reportCode", target = "")
    @Mapping(source = "shipQty", target = "quantity")
    // @Mapping(source = "oldItem", target = "")
    ClaimBillItemData toClaimBillItemData(TXfOriginClaimItemSamsEntity tXfOriginClaimItemSamsEntity);

}
