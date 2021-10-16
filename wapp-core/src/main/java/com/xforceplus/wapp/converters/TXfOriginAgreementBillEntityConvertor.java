package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.model.AgreementBillData;
import com.xforceplus.wapp.repository.entity.TXfOriginAgreementBillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TXfOriginAgreementBillEntityConvertor {

    TXfOriginAgreementBillEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginAgreementBillEntityConvertor.class);

    /**
     * @param tXfOriginAgreementBillEntity
     * @return
     */
    @Mapping(target = "businessType", constant = "2")
    // 客户编码
    @Mapping(source = "customerNumber", target = "sellerNo")
    // 客户名称
    @Mapping(source = "customerName", target = "sellerName")
    // 金额(含税)
    @Mapping(source = "amountWithTax", target = "amountWithTax")
    // 协议类型编码
    @Mapping(source = "reasonCode", target = "reasonCode")
    // 协议号
    @Mapping(source = "reference", target = "reference")
    // 税码
    @Mapping(source = "taxCode", target = "taxCode")
    // 扣款日期
    @Mapping(source = "clearingDate", target = "deductDate")
    // 税率
    @Mapping(source = "taxRate", target = "taxRate")
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
    @Mapping(source = "postingDate", target = "postingDate")
    // 税额
    @Mapping(source = "taxAmount", target = "taxAmount")
    AgreementBillData toAgreementBillData(TXfOriginAgreementBillEntity tXfOriginAgreementBillEntity);

}
