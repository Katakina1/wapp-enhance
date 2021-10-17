package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.model.ClaimBillData;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimBillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TXfOriginClaimBillEntityConvertor {

    TXfOriginClaimBillEntityConvertor INSTANCE = Mappers.getMapper(TXfOriginClaimBillEntityConvertor.class);

    // 业务单据类型;1:索赔;2:协议;3:EPD
    @Mapping(target = "businessType", constant = "1")
    // 扣款日期
    @Mapping(source = "deductionDate", target = "deductDate")
    // 扣款公司
    @Mapping(source = "deductionCompany", target = "purchaserNo")
    // 供应商号
    @Mapping(source = "vendorNo", target = "sellerNo")
    // 备注
    @Mapping(source = "remark", target = "remark")
    // 索赔号/换货号
    @Mapping(source = "exchangeNo", target = "businessNo")
    // 定案日期
    @Mapping(source = "decisionDate", target = "verdictDate")
    // 成本金额
    @Mapping(source = "costAmount", target = "amountWithoutTax")
    // 所扣发票
    @Mapping(source = "invoiceReference", target = "invoiceReference")
    // 税率
    @Mapping(source = "taxRate", target = "taxRate")
    // 含税金额
    @Mapping(source = "amountWithTax", target = "amountWithTax")
    // 店铺类型（Hyper或Sams）
    @Mapping(source = "storeType", target = "storeType")
    /**
     * 转换成ClaimBillData
     *
     * @param tXfOriginClaimBillEntity
     * @return
     */
    ClaimBillData toClaimBillData(TXfOriginClaimBillEntity tXfOriginClaimBillEntity);

}
