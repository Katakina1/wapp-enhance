package com.xforceplus.wapp.converters;

import com.google.common.collect.ImmutableMap;
import com.xforceplus.wapp.handle.vo.InvoiceVo;
import com.xforceplus.wapp.repository.entity.TXfInvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface InvoiceConverter {
    @Mapping(target = "paperDrewDate", source = "paperDrewDate", dateFormat = "yyyyMMdd")
    @Mapping(target = "invoiceType", source = "invoiceType", qualifiedByName = "mapInvoiceType")
    TXfInvoiceEntity map(InvoiceVo.Invoice data);

    Map<String, String> INVOICE_TYPE_MAP = ImmutableMap.<String, String>builder()
            .put("01-10-01", "01").put("02-60-01", "03").put("02-10-01", "04").put("01-10-06", "08")
            .put("02-10-06", "10").put("02-10-02", "11").put("02-20-06", "14").build();

    @Named("mapInvoiceType")
    default String mapInvoiceType(String status) {
//      01 01-10-01、03 02-60-01、04 02-10-01、08 01-10-06、10 02-10-06、11 02-10-02、14 02-20-06
        return INVOICE_TYPE_MAP.get(status);
    }
}
