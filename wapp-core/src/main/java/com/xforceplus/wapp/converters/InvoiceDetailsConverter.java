package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.client.TaxWareInvoiceDetail;
import com.xforceplus.wapp.repository.entity.InvoiceDetailsEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class)
public interface InvoiceDetailsConverter {
    List<InvoiceDetailsEntity> map(List<TaxWareInvoiceDetail> invoices);
}
