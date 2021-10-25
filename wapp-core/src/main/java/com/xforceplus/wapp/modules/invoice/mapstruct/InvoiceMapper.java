package com.xforceplus.wapp.modules.invoice.mapstruct;


import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceItemDto;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    InvoiceDto entityToInvoiceDto(TDxRecordInvoiceEntity tXfInvoiceEntity);

    InvoiceItemDto entityToInvoiceItemDto(TDxRecordInvoiceDetailEntity tXfInvoiceItemEntity);

    List<InvoiceItemDto> entityToInvoiceItemDtoList(List<TDxRecordInvoiceDetailEntity> tXfInvoiceItemEntityList);

}
