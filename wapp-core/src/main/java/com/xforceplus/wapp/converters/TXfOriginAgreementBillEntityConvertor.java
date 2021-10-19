package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.model.AgreementBillData;
import com.xforceplus.wapp.modules.job.command.EpdBillFilterCommand;
import com.xforceplus.wapp.repository.entity.TXfOriginAgreementBillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = EpdBillFilterCommand.class)
public interface TXfOriginAgreementBillEntityConvertor {

    TXfOriginAgreementBillEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginAgreementBillEntityConvertor.class);

    // 业务单据类型;1:索赔;2:协议;3:EPD
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
    @Mapping(source = "clearingDate", target = "deductDate", dateFormat = "yyyy/MM/dd")
    // 税率
    @Mapping(target = "taxRate", defaultValue = "java(EpdBillFilterCommand.TAX_CODE_TRANSLATOR.get(tXfOriginAgreementBillEntity.getTaxCode()))")
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
    @Mapping(source = "taxAmount", target = "taxAmount")
    /**
     * 转换成AgreementBillData
     *
     * @param tXfOriginAgreementBillEntity
     * @return
     */
    AgreementBillData toAgreementBillData(TXfOriginAgreementBillEntity tXfOriginAgreementBillEntity);

}
