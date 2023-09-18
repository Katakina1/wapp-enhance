package com.xforceplus.wapp.modules.exchange.model;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface ExchangeExportConverter {


    List<ExSupplierchangeExportDto> exportMap(List<InvoiceExchangeResponse> list);
}
