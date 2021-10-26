package com.xforceplus.wapp.converters;

import com.google.common.collect.ImmutableMap;
import com.xforceplus.wapp.modules.deduct.model.AgreementBillData;
import com.xforceplus.wapp.repository.entity.TXfOriginAgreementBillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Map;

@Mapper
public interface TXfOriginAgreementBillEntityConvertor {

    TXfOriginAgreementBillEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginAgreementBillEntityConvertor.class);

    Map<String, BigDecimal> TAX_CODE_TRANSLATOR =
            ImmutableMap
                    .<String, BigDecimal>builder()
                    .put("TG", BigDecimal.valueOf(0.03))
                    .put("TH", BigDecimal.valueOf(0.17))
                    .put("TL", BigDecimal.valueOf(0.11))
                    .put("TM", BigDecimal.valueOf(0.16))
                    .put("TN", BigDecimal.valueOf(0.10))
                    .put("TO", BigDecimal.valueOf(0.13))
                    .put("TP", BigDecimal.valueOf(0.09))
                    .build();

    // 业务单据类型;1:索赔;2:协议;3:EPD
    @Mapping(target = "businessType", constant = "2")
    // 客户编码
    @Mapping(source = "customerNumber", target = "sellerNo")
    // 客户名称
    @Mapping(source = "customerName", target = "sellerName")
    // 金额(含税)
    @Mapping(target = "amountWithTax", expression = "java(parse(tXfOriginAgreementBillEntity.getAmountWithTax(),0))")
    // 协议类型编码
    @Mapping(source = "reasonCode", target = "reasonCode")
    // 协议号
    @Mapping(source = "reference", target = "reference")
    // 税码
    @Mapping(source = "taxCode", target = "taxCode")
    // 扣款日期
    @Mapping(source = "clearingDate", target = "deductDate", dateFormat = "yyyy/MM/dd")
    // 税率
    @Mapping(target = "taxRate", expression = "java(TAX_CODE_TRANSLATOR.get(tXfOriginAgreementBillEntity.getTaxCode()))")
    // 供应商6D
    @Mapping(source = "memo", target = "memo")
    // 协议类型
    @Mapping(source = "referenceType", target = "referenceType")
    // 扣款公司编码
    @Mapping(source = "companyCode", target = "purchaserNo")
    // 凭证编号
    @Mapping(source = "documentNumber", target = "documentNo")
    // 凭证类型
    @Mapping(source = "documentType", target = "documentType")
    // 入账日期
    @Mapping(source = "postingDate", target = "postingDate", dateFormat = "yyyy/MM/dd")
    // 税额
    @Mapping(target = "taxAmount", expression = "java(parse(tXfOriginAgreementBillEntity.getTaxAmount(),0))")
    /**
     * 转换成AgreementBillData
     *
     * @param tXfOriginAgreementBillEntity
     * @return
     */
    AgreementBillData toAgreementBillData(TXfOriginAgreementBillEntity tXfOriginAgreementBillEntity);

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
