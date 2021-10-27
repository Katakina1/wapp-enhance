package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemSamsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Objects;

@Mapper
public interface TXfOriginClaimItemSamsEntityConvertor {

    TXfOriginClaimItemSamsEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginClaimItemSamsEntityConvertor.class);

    @Mapping(source = "primaryDesc", target = "cnDesc")
    @Mapping(source = "itemTaxPct", target = "taxRate")
    @Mapping(target = "amountWithoutTax", expression = "java(parse(tXfOriginClaimItemSamsEntity.getShipCost(),0))")
    @Mapping(source = "itemNbr", target = "itemNo")
    @Mapping(source = "rtnDate", target = "verdictDate", dateFormat = "yyyy/MM/dd")
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
    @Mapping(source = "claimNumber", target = "claimNo")
    // @Mapping(source = "oldItem", target = "")
    @Mapping(target = "price", expression = "java(calcPrice(tXfOriginClaimItemSamsEntity.getShipQty(),tXfOriginClaimItemSamsEntity.getShipCost()))")
    /**
     * @param tXfOriginClaimItemSamsEntity
     * @return
     */
    ClaimBillItemData toClaimBillItemData(TXfOriginClaimItemSamsEntity tXfOriginClaimItemSamsEntity);

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
        try {
            ParsePosition position = new ParsePosition(positionIndex);
            return (BigDecimal) format.parse(number, position);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据数量和不含税金额反算单价
     *
     * @param shipQty
     * @param shipCost
     * @return
     */
    default BigDecimal calcPrice(String shipQty, String shipCost) {
        BigDecimal amountWithoutTax = parse(shipCost, 0);
        BigDecimal quantity = parse(shipQty, 0);
        if (Objects.nonNull(amountWithoutTax) && Objects.nonNull(quantity)) {
            try {
                return amountWithoutTax.divide(quantity, 15, RoundingMode.HALF_UP).abs();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
