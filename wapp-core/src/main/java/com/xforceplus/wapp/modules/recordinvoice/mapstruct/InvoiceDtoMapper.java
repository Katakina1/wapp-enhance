package com.xforceplus.wapp.modules.recordinvoice.mapstruct;

import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import org.mapstruct.Mapper;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-25 14:29
 **/
@Mapper(componentModel = "spring")
public interface InvoiceDtoMapper {
    InvoiceDto toDto(TDxRecordInvoiceEntity entity);

}
