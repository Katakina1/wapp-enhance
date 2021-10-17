package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.model.EPDBillData;
import com.xforceplus.wapp.repository.entity.TXfOriginEpdBillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TXfOriginEpdBillEntityConvertor {

    TXfOriginEpdBillEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginEpdBillEntityConvertor.class);

    // 业务单据类型;1:索赔;2:协议;3:EPD
    @Mapping(target = "businessType", constant = "3")
    @Mapping(source = "taxCode", target = "taxCode")
    @Mapping(source = "documentType", target = "documentType")
    // @Mapping(source = "referenceKey1", target = "")
    // @Mapping(source = "cashDiscAmtLc", target = "")
    @Mapping(source = "account", target = "sellerNo")
    @Mapping(source = "clearingDate", target = "deductDate")
    @Mapping(source = "amountInLocalCurrency", target = "amountWithTax")
    // @Mapping(source = "referenceKey2", target = "")
    // @Mapping(source = "reverseClearing", target = "")
    @Mapping(source = "reference", target = "businessNo")
    // @Mapping(source = "paymentBlock", target = "")
    @Mapping(source = "postingDate", target = "postingDate")
    @Mapping(source = "invoiceReference", target = "invoiceReference")
    @Mapping(source = "paymentDate", target = "paymentDate")
    @Mapping(source = "companyCode", target = "purchaserNo")
    @Mapping(source = "text", target = "remark")
    /**
     * 转换成EPDBillData
     *
     * @param tXfOriginEpdBillEntity
     * @return
     */
    EPDBillData toEpdBillData(TXfOriginEpdBillEntity tXfOriginEpdBillEntity);

}
