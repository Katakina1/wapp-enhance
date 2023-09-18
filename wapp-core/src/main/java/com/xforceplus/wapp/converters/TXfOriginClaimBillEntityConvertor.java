package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.model.ClaimBillData;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimBillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;

@Mapper(uses = BaseConverter.class, imports = {BigDecimal.class})
public interface TXfOriginClaimBillEntityConvertor {

    TXfOriginClaimBillEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginClaimBillEntityConvertor.class);

    // 业务单据类型;1:索赔;2:协议;3:EPD
    @Mapping(target = "businessType", constant = "1")
    // 扣款日期
    @Mapping(source = "deductionDate", target = "deductDate", dateFormat = "yyyy/MM/dd")
    // 扣款公司
    @Mapping(source = "deductionCompany", target = "purchaserNo")
    // 供应商号
    @Mapping(source = "vendorNo", target = "sellerNo")
    // 备注
    @Mapping(source = "remark", target = "remark")
    // 索赔号/换货号
    @Mapping(source = "exchangeNo", target = "businessNo")
    // 定案日期
    @Mapping(source = "decisionDate", target = "verdictDate", dateFormat = "yyyy/MM/dd")
    // 成本金额
    @Mapping(target = "amountWithoutTax", expression = "java(parse(tXfOriginClaimBillEntity.getCostAmount(),0))")
    // 所扣发票
    @Mapping(source = "invoiceReference", target = "invoiceReference")
    // 税率
    @Mapping(source = "taxRate", target = "taxRate")
    // 含税金额
    @Mapping(target = "amountWithTax", expression = "java(parse(tXfOriginClaimBillEntity.getAmountWithTax(),0))")
    // 店铺类型（Hyper或Sams）
    @Mapping(source = "storeType", target = "storeType")
    /**
     * 转换成ClaimBillData
     *
     * @param tXfOriginClaimBillEntity
     * @return
     */
    ClaimBillData toClaimBillData(TXfOriginClaimBillEntity tXfOriginClaimBillEntity);

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

