package com.xforceplus.wapp.modules.invoice.mapstruct;


import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceItemDto;
import com.xforceplus.wapp.repository.entity.TDxInvoiceDetailsEntity;
import com.xforceplus.wapp.repository.entity.TDxInvoiceEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    InvoiceDto entityToInvoiceDto(TDxInvoiceEntity tXfInvoiceEntity);

    InvoiceItemDto entityToInvoiceItemDto(TDxInvoiceDetailsEntity tXfInvoiceItemEntity);

    List<InvoiceItemDto> entityToInvoiceItemDtoList(List<TDxInvoiceDetailsEntity> tXfInvoiceItemEntityList);

}
