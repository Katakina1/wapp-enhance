package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemHyperEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TXfOriginClaimItemHyperEntityConvertor {

    TXfOriginClaimItemHyperEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginClaimItemHyperEntityConvertor.class);

    @Mapping(source = "upcNbr", target = "upc")
    @Mapping(source = "unitCost", target = "price")
    // @Mapping(source = "vendorStockId", target = "")
    // @Mapping(source = "claimNbr", target = "")
    @Mapping(source = "vndrNbr", target = "sellerNo")
    @Mapping(source = "deptNbr", target = "deptNbr")
    @Mapping(source = "taxRate", target = "taxRate")
    @Mapping(source = "finalDate", target = "verdictDate", dateFormat = "yyyy/MM/dd")
    @Mapping(source = "categoryNbr", target = "categoryNbr")
    @Mapping(source = "vnpkCost", target = "vnpkCost")
    @Mapping(source = "itemQty", target = "quantity")
    @Mapping(source = "lineCost", target = "amountWithoutTax")
    @Mapping(source = "vnpkQty", target = "vnpkQuantity")
    @Mapping(source = "cnDesc", target = "cnDesc")
    @Mapping(source = "itemNbr", target = "itemNo")
    /**
     * @param tXfOriginClaimItemHyperEntity
     * @return
     */
    ClaimBillItemData toClaimBillItemData(TXfOriginClaimItemHyperEntity tXfOriginClaimItemHyperEntity);

}
