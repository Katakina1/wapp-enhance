package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.client.TaxWareInvoice;
import com.xforceplus.wapp.repository.entity.InvoiceEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class)
public interface InvoiceConverter {
    List<InvoiceEntity> map(List<TaxWareInvoice> invoices);
}
