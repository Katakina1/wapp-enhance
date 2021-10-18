package com.xforceplus.wapp.converters;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.xforceplus.wapp.client.TaxWareInvoice;
import com.xforceplus.wapp.repository.entity.RecordInvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface InvoiceConverter {
    List<RecordInvoiceEntity> map(List<TaxWareInvoice> invoices);

    @Mapping(target = "invoiceStatus", source = "status", qualifiedByName = "mapTaxWareStatus")
    @Mapping(target = "invoiceType", source = "invoiceType", qualifiedByName = "mapTaxWareInvoiceType")
    @Mapping(target = "invoiceDate", source = "invoiceDate", qualifiedByName = "formatYMD")
    RecordInvoiceEntity map(TaxWareInvoice invoices);

    @Named("mapTaxWareStatus")
    default String mapTaxWareStatus(String status) {
//        底账发票状态:0作废,1正常,2红冲,-1失控,-2异常,-9未知
//        库发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常 5-蓝冲
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put("0", "2").put("1", "0").put("2", "3").put("-1", "1").put("-2", "4").put("-9", "9").build();
        return map.get(status);
    }

    @Named("mapTaxWareInvoiceType")
    default String mapTaxWareInvoiceType(String type) {
//        底账 增值税电子普通发票 ce、 增值税电子专用发票 se、 增值税普通发票 c、 增值税专用发票 s 、卷票 ju、通行费 ct
//        库 发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
        // s-01、se-10、ce-10、ju-11、ct-14
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put("s", "01").put("se", "10").put("ce", "10").put("ju", "11").put("ct", "14").build();
        return map.get(type);
    }
}
