package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.handle.vo.InvoiceVo;
import com.xforceplus.wapp.repository.entity.TXfInvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface InvoiceConverter {
    @Mapping(target = "paperDrewDate", source = "paperDrewDate", dateFormat = "yyyyMMdd")
    TXfInvoiceEntity map(InvoiceVo.Invoice data);
}
