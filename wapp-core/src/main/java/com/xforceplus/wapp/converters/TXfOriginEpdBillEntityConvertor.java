package com.xforceplus.wapp.converters;

import com.google.common.collect.ImmutableMap;
import com.xforceplus.wapp.modules.deduct.model.EPDBillData;
import com.xforceplus.wapp.repository.entity.TXfOriginEpdBillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Map;

@Mapper
public interface TXfOriginEpdBillEntityConvertor {

    TXfOriginEpdBillEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginEpdBillEntityConvertor.class);

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
    @Mapping(target = "businessType", constant = "3")
    @Mapping(source = "taxCode", target = "taxCode")
    @Mapping(source = "documentType", target = "documentType")
    // @Mapping(source = "referenceKey1", target = "")
    // @Mapping(source = "cashDiscAmtLc", target = "")
    @Mapping(source = "account", target = "sellerNo")
    @Mapping(source = "clearingDate", target = "deductDate", dateFormat = "yyyy/MM/dd")
    @Mapping(source = "amountInLocalCurrency", target = "amountWithTax")
    // @Mapping(source = "referenceKey2", target = "")
    // @Mapping(source = "reverseClearing", target = "")
    @Mapping(source = "reference", target = "reference")
    // @Mapping(source = "paymentBlock", target = "")
    @Mapping(source = "postingDate", target = "postingDate", dateFormat = "yyyy/MM/dd")
    @Mapping(source = "invoiceReference", target = "invoiceReference")
    @Mapping(source = "paymentDate", target = "paymentDate", dateFormat = "yyyy/MM/dd")
    @Mapping(source = "companyCode", target = "purchaserNo")
    @Mapping(source = "text", target = "remark")
    @Mapping(target = "taxRate", expression = "java(TAX_CODE_TRANSLATOR.get(tXfOriginEpdBillEntity.getTaxCode()))")
    /**
     * 转换成EPDBillData
     *
     * @param tXfOriginEpdBillEntity
     * @return
     */
    EPDBillData toEpdBillData(TXfOriginEpdBillEntity tXfOriginEpdBillEntity);

}
