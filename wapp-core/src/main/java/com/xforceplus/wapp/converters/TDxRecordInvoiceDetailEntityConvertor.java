package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.service.BlueInvoiceService;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @program: wapp-generator
 * @description: convert TXfInvoiceItemEntity
 * @author: Kenny Wong
 * @create: 2021-10-20 16:57
 **/
@Mapper(uses = BaseConverter.class)
public interface TDxRecordInvoiceDetailEntityConvertor {

    TDxRecordInvoiceDetailEntityConvertor INSTANCE = Mappers.getMapper(TDxRecordInvoiceDetailEntityConvertor.class);

    // @Mapping(source = "", target = "itemNo")
    // 发票代码
    @Mapping(source = "invoiceCode", target = "invoiceCode")
    // 发票号码
    @Mapping(source = "invoiceNo", target = "invoiceNo")
    // 明细序号
    @Mapping(source = "detailNo", target = "detailNo")
    // 货物或应税劳务名称
    @Mapping(source = "goodsName", target = "goodsName")
    // 规格型号
    @Mapping(source = "model", target = "model")
    // 单位
    @Mapping(source = "unit", target = "unit")
    // 数量
    @Mapping(  target = "num" ,source = "num",qualifiedByName = "stringToBgDcmlOrZero")
    // 单价
    @Mapping(  target = "unitPrice",source = "unitPrice",qualifiedByName = "stringToBgDcmlOrZero")
    // 金额
    @Mapping(source = "detailAmount", target = "detailAmount")
    // 税率
    @Mapping(source = "taxRate", target = "taxRate")
    // 税额
    @Mapping(source = "taxAmount", target = "taxAmount")
    // 商品编码
    @Mapping(source = "goodsNum", target = "goodsNum")

    @Mapping(source = "id", target = "itemId")
    /**
     * 转换成BlueInvoiceService.InvoiceItem
     *
     * @param invoiceItem
     * @return
     */
    BlueInvoiceService.InvoiceItem toSettlementItem(TDxRecordInvoiceDetailEntity tXfInvoiceItemEntity);

    @Named("stringToBgDcmlOrZero")
    default BigDecimal stringToBgDcmlOrZero(String str){
        if (StringUtils.isBlank(str)){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(str);
    }
}

