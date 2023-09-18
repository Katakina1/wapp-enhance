package com.xforceplus.wapp.converters;

import com.google.common.collect.ImmutableMap;
import com.xforceplus.wapp.modules.deduct.model.AgreementBillData;
import com.xforceplus.wapp.repository.entity.TXfOriginAgreementMergeEntity;
import org.mapstruct.Mapper;
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
                    .put("T139", BigDecimal.valueOf(0.13))
                    .put("T913", BigDecimal.valueOf(0.09))
                    .build();

   Map<String,String> REASON_CODE_MAP = ImmutableMap
           .<String, String>builder()
           .put("542", "全渠道推广营销服务费")
           .put("539", "营销服务")
           .put("538", "电子优惠券折扣补偿")
           .put("549", "信息系统服务费")
           .put("504", "广告服务")
           .put("530", "展示促销违约金")
           .put("540", "展示促销费")
           .put("532", "逾期未领取退货违约金")
           .put("536", "运输服务费（进仓）")
           .put("500", "彩页")
           .put("Z01", "咨询服务费")
           .put("Z14", "服务费-查询费")
           .put("Z03", "推广服务费")
           .put("Z04", "推广服务费")
           .put("Z05", "推广服务费")
           .put("509", "不诚实行为违约金")
           .put("512", "销售返利 （另行约定）")
           .put("519", "降价")
           .put("528", "无退货折扣（临时）")
           .put("529", "商品瑕疵违约金（门店）")
           .put("506", "折扣券补偿")
           .put("526", "联合促销补偿")
           .put("511", "销售返利（合同约定）")
           .put("527", "无退货折扣")
           .put("537", "降价（山姆仓包）")
           .put("533", "商品瑕疵违约金（总部）")
           .put("534", "不诚实行为门店损失赔偿")
           .put("514", "未送齐货违约金")
           .put("531", "未按预约时间送货违约金")
           .put("507", "运输及配送服务折扣")
           .put("544", "促销人员服务费")
           .put("543", "迟延提供数字化商品信息违约金")
           .put("553", "供应商库存管理费")
           .put("556", "数据使用服务费")
           .put("552", "全渠道广告费")
           .put("554", "物流辅助服务费")
           .put("551", "品牌洞察服务费")
           .put("541", "推广服务费")
           .put("072", "彩页")
           .put("72", "彩页")
           .put("188", "广告")
           .put("591", "逾期未领取退货违约金")
           .put("641", "多媒体促销服务费")
           .put("287", "展示促销违约金")
           .put("663", "进仓运输服务费")
           .put("430", "不诚实行为违约金")
           .put("206", "销售返利")
           .put("046", "折扣券补偿")
           .put("46", "折扣券补偿")
           .put("057", "降价")
           .put("57", "降价")
           .put("548", "联合促销补偿")
           .put("033", "无退货折扣")
           .put("33", "无退货折扣")
           .put("317", "无退货折扣")
           .put("064", "商品瑕疵违约金")
           .put("64", "商品瑕疵违约金")
           .put("286", "未妥当送货/未送货违约金")
           .put("290", "未按预约时间送货违约金")
           .put("589", "运输及配送服务折扣")
           .put("653", "商品瑕疵违约金(总部）")
           .put("651", "不诚实行为门店损失赔偿")
           .put("182", "政府检查费用(已停用）")
           .put("Z09", "价格补偿")
           .build();

    // // 业务单据类型;1:索赔;2:协议;3:EPD
    // @Mapping(target = "businessType", constant = "2")
    // // 客户编码
    // @Mapping(source = "customerNumber", target = "sellerNo")
    // // 客户名称
    // @Mapping(source = "customerName", target = "sellerName")
    // // 金额(含税)
    // @Mapping(target = "amountWithTax", expression = "java(parse(tXfOriginAgreementBillEntity.getAmountWithTax(),0))")
    // // 协议类型编码
    // @Mapping(source = "reasonCode", target = "reasonCode")
    // // 协议号
    // @Mapping(source = "reference", target = "reference")
    // // 税码
    // @Mapping(source = "taxCode", target = "taxCode")
    // // 扣款日期
    // @Mapping(source = "clearingDate", target = "deductDate", dateFormat = "yyyy/MM/dd")
    // // 税率
    // @Mapping(target = "taxRate", expression = "java(TAX_CODE_TRANSLATOR.get(tXfOriginAgreementBillEntity.getTaxCode()))")
    // // 供应商6D
    // @Mapping(source = "memo", target = "memo")
    // // 协议类型
    // @Mapping(source = "referenceType", target = "referenceType")
    // // 扣款公司编码
    // @Mapping(source = "companyCode", target = "purchaserNo")
    // // 凭证编号
    // @Mapping(source = "documentNumber", target = "documentNo")
    // // 凭证类型
    // @Mapping(source = "documentType", target = "documentType")
    // // 入账日期
    // @Mapping(source = "postingDate", target = "postingDate", dateFormat = "yyyy/MM/dd")
    // // 税额
    // @Mapping(target = "taxAmount", expression = "java(parse(tXfOriginAgreementBillEntity.getTaxAmount(),0))")
    /**
     * 转换成AgreementBillData
     *
     * @param mergeTmpEntity
     * @return
     */
    AgreementBillData toAgreementBillData(TXfOriginAgreementMergeEntity mergeTmpEntity);

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
