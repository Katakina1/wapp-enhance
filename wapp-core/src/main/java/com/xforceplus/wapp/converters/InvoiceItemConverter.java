package com.xforceplus.wapp.converters;

import com.google.common.collect.Lists;
import com.xforceplus.wapp.handle.vo.InvoiceVo;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface InvoiceItemConverter {
    TDxRecordInvoiceDetailEntity map(InvoiceVo.InvoiceItemVO items, Long invoiceId);

    default List<TDxRecordInvoiceDetailEntity> map(List<InvoiceVo.InvoiceItemVO> items, Long invoiceId) {
        List<TDxRecordInvoiceDetailEntity> list = Lists.newArrayListWithCapacity(items.size());
        for (InvoiceVo.InvoiceItemVO item : items) {
            list.add(map(item, invoiceId));
        }
        return list;
    }
}
