package com.xforceplus.wapp.modules.backfill.service;

import com.xforceplus.wapp.modules.backfill.model.UploadResult;
import com.xforceplus.wapp.repository.entity.InvoiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SucceedInvoiceMapper {

    UploadResult.SucceedInvoice toSucceed(InvoiceEntity invoiceEntity);
}
