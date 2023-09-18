package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemHyperEntity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;

@Mapper(uses = BaseConverter.class, imports = {BigDecimal.class})
public interface TXfOriginClaimItemHyperEntityConvertor {

    TXfOriginClaimItemHyperEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginClaimItemHyperEntityConvertor.class);

    @Mapping(target = "upc",expression = "java(com.xforceplus.wapp.converters.Converter.convertUpc(tXfOriginClaimItemHyperEntity.getUpcNbr()))")
    @Mapping( target = "price",expression = "java(new BigDecimal(tXfOriginClaimItemHyperEntity.getUnitCost()).abs())")
    // @Mapping(source = "vendorStockId", target = "")
    // @Mapping(source = "claimNbr", target = "")
    @Mapping(source = "vndrNbr", target = "sellerNo",qualifiedByName = "parseSellerNo")
    @Mapping(source = "deptNbr", target = "deptNbr")
    @Mapping(target = "taxRate", expression = "java(com.xforceplus.wapp.converters.Converter.convertTaxRate(tXfOriginClaimItemHyperEntity.getTaxRate()))")
    @Mapping(source = "finalDate", target = "verdictDate", dateFormat = "yyyy/MM/dd")
    @Mapping(source = "categoryNbr", target = "categoryNbr")
    @Mapping(source = "vnpkCost", target = "vnpkCost")
    @Mapping(target = "quantity",expression = "java(new BigDecimal(tXfOriginClaimItemHyperEntity.getItemQty()).abs())")
//    @Mapping(target = "amountWithoutTax", expression = "java(parse(tXfOriginClaimItemHyperEntity.getLineCost()))")
    @Mapping(target = "amountWithoutTax",  qualifiedByName = "parseAmount",source = "lineCost")
    @Mapping(source = "vnpkQty", target = "vnpkQuantity")
    @Mapping(source = "cnDesc", target = "cnDesc")
    @Mapping(source = "itemNbr", target = "itemNo")
    @Mapping(source = "itemNbr", target = "itemNbr")
    @Mapping( target = "storeNbr" ,expression = "java(com.xforceplus.wapp.common.utils.CommonUtil.fillZero(tXfOriginClaimItemHyperEntity.getStoreNbr()))")
    @Mapping(source = "claimNbr", target = "claimNo")
    /**
     * @param tXfOriginClaimItemHyperEntity
     * @return
     */
    ClaimBillItemData toClaimBillItemData(TXfOriginClaimItemHyperEntity tXfOriginClaimItemHyperEntity);


    @Named("parseSellerNo")
    default String parseSellerNo(String vndrNbr){
        if (StringUtils.isNotBlank(vndrNbr)){
            if (vndrNbr.length()>3){
                final String sellerNo = vndrNbr.substring(0, vndrNbr.length() - 3);
                return CommonUtil.fillZero(sellerNo);
            }
        }
        return vndrNbr;
    }

    /**
     * 将数字类型的字符串（可能含千分符）转换成数字
     *
     * @param number
     * @return
     */
    @Named("parseAmount")
    default BigDecimal parse(String lineCost) {
        int positionIndex = 0;
        DecimalFormat format = new DecimalFormat();
        format.setParseBigDecimal(true);
        ParsePosition position = new ParsePosition(positionIndex);
        final BigDecimal parse = (BigDecimal) format.parse(lineCost, position);
        return parse.abs();
    }

//    default String parseUpc(String upc){
//        if (upc.endsWith("0")){
//            return "H"+upc.substring(0,upc.lastIndexOf("0"));
//        }
//        return upc;
//    }
}

