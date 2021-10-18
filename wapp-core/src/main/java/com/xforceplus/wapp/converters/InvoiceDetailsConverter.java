package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.client.TaxWareInvoiceDetail;
import com.xforceplus.wapp.repository.entity.RecordInvoiceDetailsEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface InvoiceDetailsConverter {
    List<RecordInvoiceDetailsEntity> map(List<TaxWareInvoiceDetail> invoices);
}
