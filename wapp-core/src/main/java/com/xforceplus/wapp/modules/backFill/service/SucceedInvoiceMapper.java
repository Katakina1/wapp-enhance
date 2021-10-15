package com.xforceplus.wapp.modules.backFill.service;

import com.xforceplus.wapp.modules.backFill.model.UploadResult;
import com.xforceplus.wapp.repository.entity.InvoiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SucceedInvoiceMapper {

    UploadResult.SucceedInvoice toSucceed(InvoiceEntity invoiceEntity);
}
