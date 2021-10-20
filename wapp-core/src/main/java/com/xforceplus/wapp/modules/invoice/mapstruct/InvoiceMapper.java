package com.xforceplus.wapp.modules.invoice.mapstruct;


import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceItemDto;
import com.xforceplus.wapp.modules.rednotification.mapstruct.RedNotificationFactory;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationItem;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.modules.rednotification.model.excl.ExportInfo;
import com.xforceplus.wapp.modules.rednotification.model.excl.ExportItemInfo;
import com.xforceplus.wapp.modules.rednotification.model.excl.ImportInfo;
import com.xforceplus.wapp.repository.entity.TXfInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceItemEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    InvoiceDto entityToInvoiceDto(TXfInvoiceEntity tXfInvoiceEntity);

    InvoiceItemDto entityToInvoiceItemDto(TXfInvoiceItemEntity tXfInvoiceItemEntity);

    List<InvoiceItemDto> entityToInvoiceItemDtoList(List<TXfInvoiceItemEntity> tXfInvoiceItemEntityList);

}
