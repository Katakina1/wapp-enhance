package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemHyperEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;

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
    @Mapping(target = "amountWithoutTax", expression = "java(parse(tXfOriginClaimItemHyperEntity.getLineCost(),0))")
    @Mapping(source = "vnpkQty", target = "vnpkQuantity")
    @Mapping(source = "cnDesc", target = "cnDesc")
    @Mapping(source = "itemNbr", target = "itemNo")
    /**
     * @param tXfOriginClaimItemHyperEntity
     * @return
     */
    ClaimBillItemData toClaimBillItemData(TXfOriginClaimItemHyperEntity tXfOriginClaimItemHyperEntity);

    /**
     * 将数字类型的字符串（可能含千分符）转换成数字
     *
     * @param number
     * @param positionIndex
     * @return
     */
    default BigDecimal parse(String number, int positionIndex) {
        DecimalFormat format = new DecimalFormat();
        format.setParseBigDecimal(true);
        ParsePosition position = new ParsePosition(positionIndex);
        return (BigDecimal) format.parse(number, position);
    }
}
