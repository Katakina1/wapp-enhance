package com.xforceplus.wapp.modules.preinvoice.mapstruct;

import com.xforceplus.wapp.modules.preinvoice.dto.PreInvoiceItem;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PreInvoiceMapper {

    PreInvoiceItem entityToPreInvoiceItemDto(TXfPreInvoiceItemEntity tXfPreInvoiceItemEntity);

    List<PreInvoiceItem> entityToPreInvoiceItemDtoList(List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntityList);

    @InheritInverseConfiguration(name = "entityToPreInvoiceItemDtoList")
    List<TXfPreInvoiceItemEntity> itemToPreInvoiceEntityList( List<PreInvoiceItem> preInvoiceItemList);

}
