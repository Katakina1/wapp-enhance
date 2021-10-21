package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.modules.deduct.service.BlueInvoiceService;
import com.xforceplus.wapp.repository.entity.TXfInvoiceItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @program: wapp-generator
 * @description: convert TXfInvoiceItemEntity
 * @author: Kenny Wong
 * @create: 2021-10-20 16:57
 **/
@Mapper
public interface TXfInvoiceItemEntityConvertor {

    TXfInvoiceItemEntityConvertor INSTANCE = Mappers.getMapper(TXfInvoiceItemEntityConvertor.class);

    // @Mapping(source = "", target = "itemNo")
    // 发票代码
    @Mapping(source = "invoiceCode", target = "invoiceCode")
    // 发票号码
    @Mapping(source = "invoiceNo", target = "invoiceNo")
    // 明细序号
    // @Mapping(source = "", target = "detailNo")
    // 货物或应税劳务名称
    @Mapping(source = "cargoName", target = "goodsName")
    // 规格型号
    @Mapping(source = "itemSpec", target = "model")
    // 单位
    @Mapping(source = "quantityUnit", target = "unit")
    // 数量
    @Mapping(source = "quantity", target = "num")
    // 单价
    @Mapping(source = "unitPrice", target = "unitPrice")
    // 金额
    @Mapping(source = "amountWithoutTax", target = "detailAmount")
    // 税率
    @Mapping(source = "taxRate", target = "taxRate")
    // 税额
    @Mapping(source = "taxAmount", target = "taxAmount")
    // 商品编码
    @Mapping(source = "cargoCode", target = "goodsNum")
    /**
     * 转换成BlueInvoiceService.InvoiceItem
     *
     * @param invoiceItem
     * @return
     */
    BlueInvoiceService.InvoiceItem toSettlementItem(TXfInvoiceItemEntity tXfInvoiceItemEntity);
}
